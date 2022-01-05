package io.zephyr.lessons.mapreduce;

import io.zephyr.api.ModuleActivator;
import io.zephyr.api.ModuleContext;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class MapReduceModuleActivator implements ModuleActivator {

  private final String[] args;
  private ConfigurableApplicationContext context;

  public MapReduceModuleActivator(String[] args) {
    System.out.println("Running with args: " + Arrays.toString(args));
    this.args = args;

  }

  public static void main(String[] args) throws Exception {
    new MapReduceModuleActivator(args).start(null);
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
