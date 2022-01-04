package io.zephyr.lessons.mapreduce;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.zephyr.kernel.concurrency.WorkerPool;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = ModuleConfiguration.class)
class MapReduceModuleActivatorTest {


  @Inject
  private WorkerPool workerPool;

  @Test
  void ensureStuffWorks() {
    assertNotNull(workerPool, "workerpool must be injected");

  }


}