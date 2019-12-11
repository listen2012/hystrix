package com.listen.hytrix.rxjava.counter;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

public class ServiceCompletionMetrics {

  private static AbstractServiceCompletionCounter counter;


  public ServiceCompletionMetrics(ServiceCompletionStream stream) {
    this.counter = new DefaultServiceStream(stream);
  }

  public long[] count() {
    return counter.getResult();
  }



}
