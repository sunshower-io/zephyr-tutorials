package io.zephyr.tutorials.testplugin2;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Tutorial2ModuleActivator implements ModuleActivator {

  private ConfigurableApplicationContext context;

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Module 2 activated!");
    SpringApplication app = new SpringApplication(Tutorial2ModuleActivator.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    this.context = app.run();
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    context.close();
    System.out.println("Module 2 deactivated!");
  }
}
