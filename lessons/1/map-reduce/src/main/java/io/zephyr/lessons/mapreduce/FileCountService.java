package io.zephyr.lessons.mapreduce;

import io.sunshower.gyre.Scope;
import java.io.IOException;

public interface FileCountService {


  void run(Scope scope) throws IOException;
}
