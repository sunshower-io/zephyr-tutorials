package io.zephyr.lessons.mapreduce.tasks;

public final class LineCount {

  final int wordCount;
  /**
   * which thread are we on
   */
  final int selector;

  final boolean finished;

  public LineCount(int wordCount, int selector, boolean finished) {
    this.wordCount = wordCount;
    this.selector = selector;
    this.finished = finished;
  }
}
