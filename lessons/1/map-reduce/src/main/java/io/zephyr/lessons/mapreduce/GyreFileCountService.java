package io.zephyr.lessons.mapreduce;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Scheduler;
import io.zephyr.kernel.concurrency.Tasks;
import io.zephyr.kernel.concurrency.WorkerPool;
import io.zephyr.lessons.mapreduce.tasks.ComputeSegmentsTask;
import io.zephyr.lessons.mapreduce.tasks.ComputeTotalsTask;
import io.zephyr.lessons.mapreduce.tasks.LineCount;
import io.zephyr.lessons.mapreduce.tasks.ParallelLineOutput;
import io.zephyr.lessons.mapreduce.tasks.WordCountTask;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.inject.Inject;

public class GyreFileCountService implements FileCountService {

  private final MapReduceOptions options;
  private final Scheduler<String> scheduler;

  private final WorkerPool workerPool;

  @Inject
  public GyreFileCountService(
      WorkerPool workerPool,
      MapReduceOptions options,
      Scheduler<String> scheduler
  ) {
    this.options = options;
    this.scheduler = scheduler;
    this.workerPool = workerPool;
  }

  @Override
  public void run(Scope scope) throws IOException {

    var t1 = System.currentTimeMillis();

    var outputDirectory = getOutputDirectory(scope);
    System.out.println("Using output directory " + outputDirectory.getAbsolutePath());

    /**
     * this really shouldn't ever get very large
     */
    var queue = new ArrayBlockingQueue<LineCount>(100);
    var output = new ParallelLineOutput(queue, options.getConcurrency(),
        outputDirectory, options);
    var future = workerPool.submit(() -> {
      output.run();
      return null;
    });

    scope.set("line-queue", queue);
    scope.set("file", options.getInputFile(scope.get("file")));
    scope.set("output-directory", outputDirectory);

    var process = Tasks.newProcess("file-count").parallel().coalesce().withContext(scope);
    var segs = new ComputeSegmentsTask();
    process.register(segs);
    var computeTotalsTask = new ComputeTotalsTask();
    var tbuilder = process.register(computeTotalsTask);

    for (int i = 0; i < options.getConcurrency(); i++) {
      var task = new WordCountTask(i);
      var taskbuilder = process.register(task);
      taskbuilder.dependsOn(segs.getName());
      tbuilder.dependsOn(task.getName());
    }

    scheduler.submit(process.create()).toCompletableFuture().join();
    var t2 = System.currentTimeMillis();
    try {
      future.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    System.out.format("Processed file in %d milliseconds\n", (t2 - t1));
    //eh--shut down
    System.exit(0);
  }

  private File getOutputDirectory(Scope scope) {
    if (options.getOutputDirectory() != null) {
      return new File(options.getOutputDirectory());
    }
    var f = scope.<File>get("output-directory");
    if (f != null) {
      return f;
    }
    return new File(System.getProperty("user.dir"));
  }
}
