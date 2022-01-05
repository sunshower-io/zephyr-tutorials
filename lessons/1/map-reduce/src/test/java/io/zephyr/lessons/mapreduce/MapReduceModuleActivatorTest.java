package io.zephyr.lessons.mapreduce;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.sunshower.gyre.Scope;
import io.sunshower.test.common.Tests;
import io.zephyr.kernel.concurrency.WorkerPool;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ModuleConfiguration.class, args = {"-c", "12"})
class MapReduceModuleActivatorTest {

  @Inject
  private WorkerPool workerPool;

  @Inject
  private ApplicationArguments arguments;

  @Test
  void ensureContextLoads() {
    assertNotNull(workerPool, "workerpool must be injected");
  }

  @Test
  void ensureOptionsAreSet(@Autowired MapReduceOptions options) {
    assertEquals(options.getConcurrency(), 12);
  }

  @Test
  void ensureMapReduceWorksWithTestFile(@Autowired FileCountService service,
      @Autowired MapReduceOptions options) throws Exception {
    var outputDirectory = Tests.createTemp();
    var scope = Scope.root();
    scope.set("output-directory", outputDirectory);
    scope.set("count", options.getConcurrency());
    scope.set("file", ClassLoader.getSystemResource("files/test").toURI().toURL().getFile());
    service.run(scope);
  }

}