package io.zephyr.lessons.mapreduce.tasks;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Task;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;


public class WordCountTask extends Task {

  private final int count;
  private Segment segment;
  private String file;

  public WordCountTask(int count) {
    super("word-count" + count);
    this.count = count;
  }

  synchronized Segment getSegment(Scope scope) {
    List<Segment> segments = scope.get("segments");
    return segments.remove(segments.size() - 1);
  }

  @Override
  public TaskValue run(Scope scope) {
    this.segment = getSegment(scope);
    this.file = scope.get("file");

    var counts = new HashMap<>();
    try (var file = new RandomAccessFile(new File(this.file), "r")) {
      file.seek(segment.start);
      var fis = new FileInputStream(file.getFD());
      var bis = new BufferedInputStream(fis);
      var count = segment.start;
      var buffer = new StringBuilder();
      while (true) {
        var ch = bis.read();
        if (ch == -1 || count == segment.end) {
          var word = buffer.toString().trim();
          counts.compute(word, (k, v) -> v == null ? 1 : (int) v + 1);
          scope.set(getName() + "values", counts);
          scope.set("word-count" + this.count, counts);
          return new TaskValue(counts, getName() + "counts");
        }
        if (Character.isWhitespace((char) ch)) {
          var word = buffer.toString().trim();
          if (!word.isEmpty()) {
            counts.compute(word, (k, v) -> v == null ? 1 : (int) v + 1);
            if (ch == '\n') {

            }
            buffer = new StringBuilder();
          }
        }
        count++;
        buffer.append((char) ch);
      }

    } catch (IOException ex) {
      System.out.println("Whoops: " + ex.getMessage());
    }
    scope.set("word-count" + this.count, counts);
    return new TaskValue(counts, getName() + "counts");
  }
}
