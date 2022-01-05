package io.zephyr.lessons.mapreduce.tasks;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ComputeTotalsTask extends Task {

  public ComputeTotalsTask() {
    super("compute-totals");
  }

  @Override
  public TaskValue run(Scope scope) {
    int count = scope.get("count");
    var results = new HashMap<String, Integer>();
    for (int i = 0; i < count; i++) {
      Map<String, Integer> taskCounts = scope.get("word-count" + i);
      if(taskCounts == null) {
        continue;
      }
      for (var entry : taskCounts.entrySet()) {
        results.compute(entry.getKey(),
            (k, v) -> v == null ? entry.getValue() : v + entry.getValue());
      }
    }
    writeAll(scope.get("output-directory"), results);
    return null;
  }

  private void writeAll(File o, HashMap<String, Integer> results) {
    if(o == null) {
      throw new IllegalArgumentException("Outut directory must not be null");
    }
    var file = new File(o, "word-counts.txt");
    if (!file.exists()) {
      try {
        if (!file.createNewFile()) {
          throw new IllegalArgumentException("Failed to create file at " + file.getAbsolutePath());
        }
      } catch (IOException ex) {
        throw new IllegalArgumentException(
            "Failed to create file at " + file.getAbsolutePath() + " reason: " + ex
                .getMessage());
      }
    }
    try (var fwriter = new FileWriter(file)) {
      for (var entry : results.entrySet()) {
        if(entry.getValue() == 1) {
          continue;
        }
        fwriter.write(entry.getKey());
        fwriter.write(":");
        fwriter.write(String.valueOf(entry.getValue()));
        fwriter.write("\n");
      }
      fwriter.flush();
    } catch (IOException ex) {
      System.out.println("Failed to write file.  Reason: " + ex.getMessage());
    }
  }
}
