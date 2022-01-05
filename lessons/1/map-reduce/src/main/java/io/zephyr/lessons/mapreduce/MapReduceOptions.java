package io.zephyr.lessons.mapreduce;

import picocli.CommandLine.Option;

public class MapReduceOptions {


  @Option(names = {"-c", "--concurrency"})
  private int concurrency;

  @Option(names = {"-i", "--input-file"})
  private String inputFile;

  public int getConcurrency() {
    return concurrency;
  }

  public String getInputFile(String defaultFile) {
    return inputFile == null ? defaultFile : inputFile;
  }
}
