package io.zephyr.lessons.mapreduce.tasks;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Task;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WordCountTask extends Task {

  static final Logger log = LoggerFactory.getLogger(WordCountTask.class);

  private final int count;
  private Segment segment;
  private String file;

  public WordCountTask(int count) {
    super("word-count" + count);
    this.count = count;
  }

  synchronized Segment getSegment(Scope scope) {
    List<Segment> segments = scope.get("segments");
    return segments.remove(0);
  }

  @Override
  public TaskValue run(Scope scope) {
    populate(scope);
    var pattern = Pattern.compile("\\s+");

    int linecount = 0;
    var counts = new HashMap<String, Long>();
    //wish java had value types for things like linecount--would reduce heap pressure
    var queue = scope.<BlockingQueue<LineCount>>get("line-queue");
    try (
        var file = new RandomAccessFile(new File(this.file), "r")
    ) {
      file.seek(segment.start);
      try (
          var inputStream = new FileInputStream(file.getFD());
          var bufferedStream = new BufferedReader(new InputStreamReader(inputStream), 1 << 20)
      ) {
        for (; ; ) {
          if (linecount++ % 100000 == 0) {
            System.out.println("Task " + this.count + " processed " + linecount + " lines");
          }
          var line = bufferedStream.readLine();
          if (line == null) {
            var lc = new LineCount(0, this.count, true);
            scope.set("word-count" + this.count, counts);
            queue.put(lc);
            return null;
          }
          var parts = pattern.split(line);
          for (var l : parts) {
            counts.compute(l, (k, v) -> v == null ? 1 : v + 1);
          }
          if (file.getFilePointer() >= segment.end || file.getFilePointer() >= file.length()) {
            var lc = new LineCount(parts.length, this.count, true);
            scope.set("word-count" + this.count, counts);
            queue.put(lc);
            System.out.println("Task " + this.count + " finished");
            return null;
          }
          var lc = new LineCount(parts.length, this.count, false);
          queue.put(lc);
        }
      }
    } catch (IOException | InterruptedException ex) {
      log.error("File operation failed: {}", ex.getMessage());
    }
    throw new IllegalStateException("Should not have hit here");
  }

  private void populate(Scope scope) {
    this.segment = getSegment(scope);
    this.file = Objects.requireNonNull(scope.get("file"), "file must not be null");
  }

//  @Override
//  public TaskValue run(Scope scope) {
//    this.segment = getSegment(scope);
//    this.file = scope.get("file");
//    BlockingQueue<LineCount> queue = scope.get("line-queue");
//
//    var counts = new HashMap<>();
//    try (var file = new RandomAccessFile(new File(this.file), "r")) {
//      file.seek(segment.start);
//      long totalCount = 0;
//      var fis = new FileInputStream(file.getFD());
//      var bis = new BufferedInputStream(fis);
//      var count = segment.start;
//      var buffer = new StringBuilder();
//      int currentCount = 0;
//      while (true) {
//        if (totalCount++ % 100000 == 0) {
//          System.out.println("Task " + this.count + " has processed " + totalCount + " words");
//        }
//        var ch = bis.read();
//        if (ch == -1 || count == segment.end) {
//          var word = buffer.toString().trim();
//          counts.compute(word, (k, v) -> v == null ? 1 : (int) v + 1);
//          scope.set(getName() + "values", counts);
//          scope.set("word-count" + this.count, counts);
//          try {
//            var lineCount = new LineCount(currentCount, this.count, true);
//            queue.put(lineCount);
//          } catch(InterruptedException ex) {
//            break;
//          }
//          return new TaskValue(counts, getName() + "counts");
//        }
//        if (Character.isWhitespace((char) ch)) {
//          currentCount++;
//          var word = buffer.toString().trim().toLowerCase(Locale.ROOT);
//          if (!word.isEmpty()) {
//            counts.compute(word, (k, v) -> v == null ? 1 : (int) v + 1);
//            if (ch == '\n') {
//              var lineCount = new LineCount(currentCount, this.count, false);
//              queue.offer(lineCount);
//              currentCount = 0;
//            }
//            buffer = new StringBuilder();
//          }
//        }
//        count++;
//        buffer.append((char) ch);
//      }
//
//    } catch (IOException ex) {
//      System.out.println("Whoops: " + ex.getMessage());
//    }
//    scope.set("word-count" + this.count, counts);
//    return new TaskValue(counts, getName() + "counts");
//  }
}
