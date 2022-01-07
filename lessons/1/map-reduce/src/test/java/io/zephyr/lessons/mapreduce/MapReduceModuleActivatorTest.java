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

@SpringBootTest(classes = ModuleConfiguration.class, args = {"-c", "4"})
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


  @Test
  void ensureMapReduceWorksWithTestFile_large(@Autowired FileCountService service,
      @Autowired MapReduceOptions options) throws Exception {
    var outputDirectory = Tests.createTemp();
    var scope = Scope.root();
    scope.set("output-directory", outputDirectory);
    scope.set("count", options.getConcurrency());
//    scope.set("file", ClassLoader.getSystemResource("files/test").toURI().toURL().getFile());
    scope.set("file", "/home/josiah/Downloads/prg/xaa");
    service.run(scope);
    // 82200001
    // 68600001
  }
}