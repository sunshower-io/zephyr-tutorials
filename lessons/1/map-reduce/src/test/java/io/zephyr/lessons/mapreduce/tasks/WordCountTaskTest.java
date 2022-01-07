package io.zephyr.lessons.mapreduce.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.sunshower.gyre.Pair;
import io.sunshower.gyre.Scope;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import org.junit.jupiter.api.Test;

class WordCountTaskTest {

  @Test
  void t() {
    System.out.println(1 << 20);
  }

  @Test
  void ensureWordCountProducesCorrectCount() throws Exception {
    var fpair = classpath("files/test");
    try (var f = fpair.snd) {
      var segments = new ArrayList<>(List.of(new Segment(0, 0, f.length())));
      var scope = Scope.root();
      scope.set("segments", segments);
      var queue = new ArrayBlockingQueue<LineCount>(100);
      scope.set("line-queue", queue);
      scope.set("file", fpair.fst.getAbsolutePath());

      var task = new WordCountTask(0);
      task.run(scope);
      Map<String, Long> result = scope.get("word-count0");
      assertNotNull(result);
      assertEquals(59, result.get("three"));
      assertEquals(100, queue.size());
    }
  }

  Pair<File, RandomAccessFile> classpath(String location) throws Exception {
    var resource = ClassLoader.getSystemResource(location);
    if (resource == null) {
      throw new IllegalStateException("Not found: " + location);
    }
    var f = new File(resource.toURI().toURL().getFile());
    return Pair.of(f, new RandomAccessFile(f, "r"));
  }

}