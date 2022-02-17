package io.zephyr.tutorials.springbase;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringBaseModuleActivator implements ModuleActivator {

  private ConfigurableApplicationContext context;

  public static void main(String[] args) {

  }

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Spring base activated!");
    SpringApplication app = new SpringApplication(SpringBaseModuleActivator.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    this.context = app.run();
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    context.stop();
    System.out.println("Spring base deactivated!");
  }
}
