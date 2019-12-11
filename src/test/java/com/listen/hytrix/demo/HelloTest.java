package com.listen.hytrix.demo;

import com.listen.hytrix.rxjava.counter.DefaultServiceStream;
import com.listen.hytrix.rxjava.counter.ServiceCompletion;
import com.listen.hytrix.rxjava.counter.ServiceCompletionMetrics;
import com.listen.hytrix.rxjava.counter.ServiceCompletionStream;
import org.junit.Test;

public class HelloTest {

  @Test
  public void test() {
    ServiceCompletionStream stream = new ServiceCompletionStream();
    ServiceCompletionMetrics metrics = new ServiceCompletionMetrics(stream);
    for (int i = 0; i < 20; i++) {
      ServiceCompletion completion = new ServiceCompletion();
      stream.write(completion);
    }
    long[] ls = metrics.count();
    for (long l : ls) {
      System.out.println(l);
    }

  }

}
