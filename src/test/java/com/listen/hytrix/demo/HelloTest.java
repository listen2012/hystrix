package com.listen.hytrix.demo;

import com.listen.hytrix.rxjava.metrics.DefaultServiceEventType;
import com.listen.hytrix.rxjava.metrics.ServiceCompletionMetrics;
import org.junit.Test;

public class HelloTest {

  @Test
  public void test() throws InterruptedException {
    new Thread(() -> {
      for (int i = 0; i < 20; i++) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        ServiceCompletionMetrics.sendEvent("hi", DefaultServiceEventType.SUCCESS);
      }
    }).start();
    for (int i = 0; i < 20; i++) {
      Thread.sleep(100);
      ServiceCompletionMetrics.sendEvent("bye", DefaultServiceEventType.FAIL);
    }

    Thread.sleep(5000L);

  }

}
