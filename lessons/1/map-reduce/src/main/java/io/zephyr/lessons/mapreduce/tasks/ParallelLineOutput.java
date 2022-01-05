package io.zephyr.lessons.mapreduce.tasks;

import java.io.File;
import java.util.Objects;

public class ParallelLineOutput implements Runnable {

  final int count;
  final File outputDirectory;

  public ParallelLineOutput(int count, File outputDirectory) {
    this.count = count;
    this.outputDirectory = Objects.requireNonNull(outputDirectory);
  }

  @Override
  public void run() {

  }
}
