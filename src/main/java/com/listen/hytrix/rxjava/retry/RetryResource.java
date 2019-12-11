package com.listen.hytrix.rxjava.retry;

import com.listen.hytrix.rxjava.retry.PushTaskObservable.WrapTask;
import io.netty.util.HashedWheelTimer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Emitter.BackpressureMode;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RetryResource {

  private static ExecutorService executor = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>());

  private static HashedWheelTimer timer = new HashedWheelTimer();

  private static AtomicInteger taskNum = new AtomicInteger(0);

  public static void addTask(Callable call) {
    PushTaskSubscribe sb = new PushTaskSubscribe();
    Observable.create(new PushTaskObservable(call), BackpressureMode.DROP).observeOn(Schedulers.from(executor))
        .subscribe(sb);
  }

  protected static void joinTimer(WrapTask task) {
    timer.newTimeout(t -> retryAgain(task), 10, TimeUnit.SECONDS);
  }

  private static void retryAgain(WrapTask task) {
    PushTaskSubscribe sb = new PushTaskSubscribe();
    Observable.create(new PushTaskObservable(task), BackpressureMode.DROP).observeOn(Schedulers.from(executor))
        .subscribe(sb);
  }

  public static Integer getTaskNum() {
    return taskNum.get();
  }

  public static Integer increaseNum() {
    return taskNum.incrementAndGet();
  }

  public static Integer decreaseNum() {
    return taskNum.decrementAndGet();
  }

}
