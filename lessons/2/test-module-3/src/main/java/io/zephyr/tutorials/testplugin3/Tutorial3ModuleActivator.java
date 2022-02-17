package io.zephyr.tutorials.testplugin3;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Tutorial3ModuleActivator implements ModuleActivator {

  private ConfigurableApplicationContext context;

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Module 3 activated!");
    SpringApplication app = new SpringApplication(Tutorial3ModuleActivator.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    this.context = app.run();
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    context.close();
    System.out.println("Module 3 deactivated!");
  }
}
