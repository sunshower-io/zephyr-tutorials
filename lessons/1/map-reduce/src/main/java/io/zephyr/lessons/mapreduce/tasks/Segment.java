package io.zephyr.lessons.mapreduce.tasks;

public final class Segment {

  final int index;
  final long end;
  final long start;

  public Segment(int index, long end, long start) {
    this.index = index;
    this.end = end;
    this.start = start;
  }
}
