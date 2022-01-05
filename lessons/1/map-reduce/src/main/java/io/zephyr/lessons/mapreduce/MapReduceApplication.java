package io.zephyr.lessons.mapreduce;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ModuleConfiguration.class)
public class MapReduceApplication {

}
