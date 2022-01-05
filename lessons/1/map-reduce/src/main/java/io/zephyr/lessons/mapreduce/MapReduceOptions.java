package io.zephyr.lessons.mapreduce;

import picocli.CommandLine.Option;

public class MapReduceOptions {


  @Option(names = {"-c", "--concurrency"})
  private int concurrency;

  @Option(names = {"-i", "--input-file"})
  private String inputFile;

  @Option(names = {"-o", "--output-directory"})
  private String outputDirectory;


  @Option(names = {"-l", "--line-buffer-size"}, defaultValue = "10000")
  private int lineBufferSize;


  public int getConcurrency() {
    return concurrency;
  }

  public String getInputFile(String defaultFile) {
    return inputFile == null ? defaultFile : inputFile;
  }

  public int getLineBufferSize() {
    return lineBufferSize;
  }

  public String getOutputDirectory() {
    return outputDirectory;
  }
}
