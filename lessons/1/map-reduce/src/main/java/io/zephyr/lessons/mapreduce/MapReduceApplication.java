package io.zephyr.lessons.mapreduce;

import io.sunshower.gyre.Scope;
import java.io.IOException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Import(ModuleConfiguration.class)
public class MapReduceApplication {

  @EventListener
  public void run(ApplicationReadyEvent event) throws IOException {
    var options = event.getApplicationContext().getBean(MapReduceOptions.class);
    System.out.println("Processing file " + options.getInputFile(null));
    var service = event.getApplicationContext().getBean(GyreFileCountService.class);
    var scope = Scope.root();
    scope.set("count", options.getConcurrency());
    service.run(scope);
  }

}
