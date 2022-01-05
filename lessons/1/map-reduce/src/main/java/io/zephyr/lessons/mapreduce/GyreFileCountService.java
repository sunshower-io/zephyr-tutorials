package io.zephyr.lessons.mapreduce;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Scheduler;
import io.zephyr.kernel.concurrency.Tasks;
import io.zephyr.lessons.mapreduce.tasks.ComputeSegmentsTask;
import io.zephyr.lessons.mapreduce.tasks.ComputeTotalsTask;
import io.zephyr.lessons.mapreduce.tasks.WordCountTask;
import javax.inject.Inject;
import org.springframework.scheduling.annotation.Async;

public class GyreFileCountService implements FileCountService {

  private final MapReduceOptions options;
  private final Scheduler<String> scheduler;


  @Inject
  public GyreFileCountService(
      MapReduceOptions options,
      Scheduler<String> scheduler
  ) {
    this.options = options;
    this.scheduler = scheduler;
  }

  @Async
  @Override
  public void run(Scope scope) {
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
  }
}
