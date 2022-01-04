package io.zephyr.lessons.mapreduce;

import io.zephyr.kernel.concurrency.ExecutorWorkerPool;
import io.zephyr.kernel.concurrency.KernelScheduler;
import io.zephyr.kernel.concurrency.Scheduler;
import io.zephyr.kernel.concurrency.WorkerPool;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModuleConfiguration {

  @Bean
  public WorkerPool workerPool() {
    return new ExecutorWorkerPool(Executors.newFixedThreadPool(32),
        Executors.newFixedThreadPool(2));
  }

  @Bean
  public Scheduler<String> scheduler(WorkerPool workerPool) {
    return new KernelScheduler<>(workerPool);
  }

}
