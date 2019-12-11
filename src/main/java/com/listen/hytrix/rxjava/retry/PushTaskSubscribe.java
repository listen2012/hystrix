package com.listen.hytrix.rxjava.retry;

import com.listen.hytrix.rxjava.Logger;
import com.listen.hytrix.rxjava.retry.PushTaskObservable.WrapTask;
import java.time.Duration;
import java.time.LocalDateTime;
import rx.Subscriber;

public class PushTaskSubscribe extends Subscriber<WrapTask> {

  @Override
  public void onCompleted() {
    System.out.println("done");
  }


  @Override
  public void onError(Throwable e) {
    //    if (e instanceof IllegalArgumentException) {
    //      Logger.info("push delay");
    //    }
  }

  @Override
  public void onNext(WrapTask c) {
    if (RetryResource.getTaskNum() > 20
        || Duration.between(c.getCreateTime(), LocalDateTime.now()).toMillis() > 40000L) {
      Logger.info("time up, remove task");
      return;
    }
    try {
      c.getCall().call();
    } catch (IllegalArgumentException ie) {
      onError(ie);
      RetryResource.joinTimer(c);
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      RetryResource.decreaseNum();
    }
  }
}
