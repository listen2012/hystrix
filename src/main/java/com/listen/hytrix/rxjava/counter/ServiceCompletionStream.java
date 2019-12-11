package com.listen.hytrix.rxjava.counter;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

public class ServiceCompletionStream {

  private SerializedSubject<ServiceCompletion, ServiceCompletion> writeOnlySubject;

  private Observable<ServiceCompletion> readOnlyStream;

  public ServiceCompletionStream() {
    this.writeOnlySubject = new SerializedSubject<>(PublishSubject.create());
    this.readOnlyStream = writeOnlySubject.share();
  }

  public Observable observe() {
    return this.readOnlyStream;
  }

  public void write(ServiceCompletion event) {
    writeOnlySubject.onNext(event);
  }


}
