package io.zephyr.lessons.mapreduce;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class MapReduceModuleActivator implements ModuleActivator {

  static String[] args;
  private ConfigurableApplicationContext context;

  public static void main(String[] args) throws Exception {
    MapReduceModuleActivator.args = args;
  }

  @Override
  public void start(ModuleContext moduleContext) throws Exception {
    this.context = SpringApplication.run(MapReduceApplication.class, args);
  }

  @Override
  public void stop(ModuleContext moduleContext) throws Exception {
    this.context.close();
  }
}
