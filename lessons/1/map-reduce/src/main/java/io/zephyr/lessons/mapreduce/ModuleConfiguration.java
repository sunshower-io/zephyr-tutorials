package io.zephyr.lessons.mapreduce;

import io.zephyr.kernel.concurrency.ExecutorWorkerPool;
import io.zephyr.kernel.concurrency.KernelScheduler;
import io.zephyr.kernel.concurrency.Scheduler;
import io.zephyr.kernel.concurrency.WorkerPool;
import java.util.concurrent.Executors;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import picocli.CommandLine;

@Configuration
public class ModuleConfiguration {

  @Bean
  public WorkerPool workerPool(MapReduceOptions options) {
    return new ExecutorWorkerPool(Executors.newFixedThreadPool(options.getConcurrency()),
        Executors.newFixedThreadPool(2));
  }

  @Bean
  public Scheduler<String> scheduler(WorkerPool workerPool) {
    return new KernelScheduler<>(workerPool);
  }

  @Bean
  public MapReduceOptions mapReduceOptions(ApplicationArguments arguments) {
    var result = new MapReduceOptions();
    var clret = new CommandLine(result).parseArgs(arguments.getSourceArgs());
    var errors = clret.errors();
    if (!errors.isEmpty()) {
      for (var error : errors) {
        System.out.println("Error: " + error.getMessage());
      }
      System.exit(1);
    }
    return result;
  }

  @Bean
  public FileCountService fileCountService(Scheduler<String> scheduler, MapReduceOptions options) {
    return new GyreFileCountService(options, scheduler);
  }

}
