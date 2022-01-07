package io.zephyr.lessons.mapreduce.tasks;

import io.sunshower.gyre.Scope;
import io.zephyr.kernel.concurrency.Task;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComputeSegmentsTask extends Task {

  private String file;
  private Integer count;

  public ComputeSegmentsTask() {
    super("compute-segments");
  }

  @Override
  public TaskValue run(Scope scope) {
    System.out.println("Computing segments...");
    this.file = scope.get("file");
    this.count = scope.get("count");
    var segments = computeSegments(count);
    scope.set("segments", segments);
    System.out.format("Done computing segments (%s)\n", count);
    return new TaskValue(segments, "computed-segments");
  }


  private List<Segment> computeSegments(int count) {
    try {
      var segments = new ArrayList<Segment>();
      try (var rafile = new RandomAccessFile(new File(file), "r")) {
        var testSegmentSize = rafile.length() / count;
        for (int i = 0; i < count; i++) {
          segments.add(computeSegment(testSegmentSize, rafile, i, segments));
        }
      }
      return Collections.synchronizedList(segments);
    } catch (IOException ex) {
      throw new IllegalStateException("Error: ", ex);
    }
  }

  private Segment computeSegment(
      long testSegmentSize, RandomAccessFile rafile, int i, List<Segment> segments)
      throws IOException {
    rafile.seek(i * testSegmentSize);
    long count = 0; // number of bytes offset
    int r = rafile.read();
    //end of file reached
    if (r == -1) {
      if (segments.isEmpty()) {
        return new Segment(i, 0, testSegmentSize + count);
      } else {
        var previousSegment = segments.get(segments.size() - 1);
        return new Segment(i, previousSegment.end + 1, (i + 1) * testSegmentSize + count);
      }
    }
    //increment until newline or EOF.  Windows newlines (CRLF) not expected
    while (!(r == -1 || r == '\n')) {
      count++;
      r = rafile.read();
    }
    if (segments.isEmpty()) {
      return new Segment(i, 0, testSegmentSize + count);

    } else {
      var seg = segments.get(segments.size() - 1);
      return new Segment(i, seg.end + 1, (i + 1) * testSegmentSize + count);
    }
  }

}
