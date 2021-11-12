package io.zephyr.lessons.helloworld;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;

public class HelloWorldModuleActivator implements ModuleActivator {


  @Override
  public void start(ModuleContext context) {
    System.out.println(String.format("Hello from %s", context.getModule().getCoordinate()));
  }

  @Override
  public void stop(ModuleContext context) {
    System.out.println(String.format("Goodbye from %s", context.getModule().getCoordinate()));
  }
}
