package io.zephyr.tutorials.springbase;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBaseModuleActivator implements ModuleActivator {

  public static void main(String[] args) {

  }

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Spring base activated!");
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    System.out.println("Spring base deactivated!");

  }
}
