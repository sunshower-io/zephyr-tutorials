package io.zephyr.tutorials.testplugin1;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;

public class Tutorial1ModuleActivator implements ModuleActivator {

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    System.out.println("Module 1 activated!");
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    System.out.println("Module 1 deactivated!");

  }
}
