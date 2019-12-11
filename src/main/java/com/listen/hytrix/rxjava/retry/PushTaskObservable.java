package com.listen.hytrix.rxjava.retry;


import com.listen.hytrix.rxjava.retry.PushTaskObservable.WrapTask;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import rx.Emitter;
import rx.functions.Action1;

public class PushTaskObservable implements Action1<Emitter<WrapTask>> {

  private static ExecutorService executor = new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS,
      new SynchronousQueue<Runnable>());

  private WrapTask wrapCall;

  public PushTaskObservable(Callable call) {
    WrapTask wrapTask = new WrapTask();
    wrapTask.call = call;
    wrapTask.createTime = LocalDateTime.now();
    this.wrapCall = wrapTask;
  }

  protected PushTaskObservable(WrapTask wrapCall) {
    this.wrapCall = wrapCall;
  }

  @Override
  public void call(Emitter<WrapTask> callableEmitter) {
    callableEmitter.onNext(wrapCall);
  }


  public class WrapTask {

    private Callable call;

    private LocalDateTime createTime;

    public Callable getCall() {
      return this.call;
    }

    public LocalDateTime getCreateTime() {
      return this.createTime;
    }
  }
}
