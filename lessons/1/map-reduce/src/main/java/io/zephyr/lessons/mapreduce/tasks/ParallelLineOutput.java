package io.zephyr.lessons.mapreduce.tasks;

import io.zephyr.lessons.mapreduce.MapReduceOptions;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class ParallelLineOutput implements Runnable {

  final int count;
  final File outputDirectory;
  final AtomicInteger remaining;
  private final BlockingQueue<LineCount> queue;
  private final MapReduceOptions options;
  private final ArrayList<LineOffsetBuffer> offsetBuffers;
  volatile long totalCount = 0;

  /**
   * we can be pretty memory-efficient here.  The index is the index of the line-count task.  Once
   * we hit options.bufferSize() we'll flush the word-counts for that file to the file, increment
   * the line-count
   */
//  private final List<LineCountWithOffset>[] lineCounts;
  public ParallelLineOutput(
      BlockingQueue<LineCount> queue, int count, File outputDirectory, MapReduceOptions options)
      throws IOException {
    this.count = count;
    this.queue = queue;
    this.outputDirectory = Objects.requireNonNull(outputDirectory);
    this.remaining = new AtomicInteger(count);
    this.offsetBuffers = new ArrayList<>(count);
    this.options = options;
    for (int i = 0; i < count; i++) {
      var file = new File(outputDirectory, String.format("word-%d.txt", i));
      if (!file.createNewFile()) {
        throw new IOException("Failed to create file: " + file);
      }
      var fileOutputStream = new DataOutputStream(
          new BufferedOutputStream(new FileOutputStream(file)));
      var buffer = new LineOffsetBuffer();
      buffer.file = file;
      buffer.currentOffset = 0;
      buffer.counts = new ArrayList<>(options.getLineBufferSize());
      buffer.writer = fileOutputStream;
      this.offsetBuffers.add(buffer);
    }
  }

  @Override
  public void run() {
    while (remaining.get() > 0) {
      try {
        handle(queue.take());
      } catch (InterruptedException e) {
        break;
      } catch (IOException ex) {
        throw new IllegalStateException("Error: buffer could not be written to.  Reason: " + ex
            .getMessage());
      }
    }
    aggregateAll();
  }

  private void handle(LineCount take) throws IOException {
    totalCount++;
    if (take.finished) {
      System.out.println("Removing task: " + take.selector);
      remaining.decrementAndGet();
    }
    var buffer = offsetBuffers.get(take.selector);
    var counts = buffer.counts;
    if (counts.size() >= options.getLineBufferSize() || take.finished) {
      long t1 = System.currentTimeMillis();
      for (int i = 0; i < counts.size(); i++) {
        buffer.writer.writeInt(counts.get(i));
      }
      counts.clear();
      long t2 = System.currentTimeMillis();
      System.out.println("Flushed records in " + (t2 - t1) + " ms");
    } else {
      buffer.counts.add(take.wordCount);
    }
  }

  private void aggregateAll() {
    System.out.println("Aggregating line-counts");
    long t1 = System.currentTimeMillis();
    var file = new File(outputDirectory, "line-counts.txt");
    try {
      if (!file.createNewFile()) {
        throw new IllegalArgumentException("Error: couldn't create aggregation file");
      }
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
    System.out.println("Writing data...");
    try (var outputs = new BufferedWriter(
        new FileWriter(file), 1 << 20)) {
      long lineCount = 0;
      for (int i = 0; i < offsetBuffers.size(); i++) {
        try (var inputStream = new DataInputStream(
            new BufferedInputStream(new FileInputStream(offsetBuffers.get(i).file), 1 << 20))) {
          while (inputStream.available() > 0) {
            outputs.write(Long.toString(lineCount));
            outputs.write(" ");
            outputs.write(Integer.toString(inputStream.readInt()));
            outputs.write("\n");
            lineCount++;
          }
          offsetBuffers.get(i).file.delete();
        }
      }
    } catch (IOException ex) {
      System.out.println("Encountered error while aggregating lines");
    }
    long t2 = System.currentTimeMillis();
    System.out.println(
        "Done aggregating line-counts in " + (t2 - t1) + " millis.  Total lines processed: "
        + totalCount);
  }



  static class LineOffsetBuffer {

    /**
     * the current offset
     */
    long currentOffset;

    /**
     * the line-counts where currentLine = currentOffset + idx
     */
    List<Integer> counts;
    /**
     * the outputstream of this file
     */
    DataOutputStream writer;
    /**
     * the file we're referring to
     */
    File file;
  }

}
