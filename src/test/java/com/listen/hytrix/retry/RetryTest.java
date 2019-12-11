package com.listen.hytrix.retry;

import com.listen.hytrix.rxjava.retry.RetryResource;
import io.netty.util.HashedWheelTimer;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class RetryTest {

  @Test
  public void test() throws InterruptedException {
    for (int i = 0; i < 5; i++) {
      final int j = i;
      new Thread(() -> RetryResource.addTask(() -> hi("hi_" + j))).start();
      Thread.sleep(30);
    }
    Thread.sleep(100000L);
  }

  private Object hi(String hi) {
    System.out.println(hi + ":" + Thread.currentThread().getId());
    throw new IllegalArgumentException("");
  }

  //  @Test
  public void task() throws InterruptedException {
    HashedWheelTimer t = new HashedWheelTimer();
    for (int i = 0; i < 5; i++) {
      t.newTimeout((l) -> hello(), 6, TimeUnit.SECONDS);
    }
    Thread.sleep(30000L);
  }

  private void hello() throws InterruptedException {
    System.out.println("hello:" + Thread.currentThread().getId() + "-" + LocalDateTime.now());
    Thread.sleep(5000L);
  }
}


