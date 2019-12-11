package com.listen.hytrix.rxjava.retry;

import com.netflix.hystrix.util.PlatformSpecific;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RetryTimer {

  private static int coreSize = 3;

  private AtomicReference<RetrySchedule> scheduleReference = new AtomicReference<>();


  public static void main(String[] args) {
    RetryTimer timer = new RetryTimer();
    timer.startSchedule();
  }

  public void startSchedule() {
    while (scheduleReference.get() == null || !scheduleReference.get().isInitialized()) {
      if (scheduleReference.compareAndSet(null, new RetrySchedule())) {
        scheduleReference.get().initialize();
      }
    }
  }

  class RetrySchedule {

    private volatile ScheduledThreadPoolExecutor executor;

    private volatile boolean initialized;

    public void initialize() {

      ThreadFactory threadFactory = null;
        threadFactory = new ThreadFactory() {
          final AtomicInteger counter = new AtomicInteger();

          @Override
          public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "RetryTimer-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
          }

        };

      executor = new ScheduledThreadPoolExecutor(coreSize, threadFactory);
      initialized = true;
    }

    public boolean isInitialized() {
      return initialized;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
      return executor;
    }

  }
}
