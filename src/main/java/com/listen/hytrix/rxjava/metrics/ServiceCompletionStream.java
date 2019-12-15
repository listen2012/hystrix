package com.listen.hytrix.rxjava.metrics;

import rx.BackpressureOverflow;
import rx.BackpressureOverflow.Strategy;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ServiceCompletionStream implements ServiceEventStream<CompletionEvent> {

    private Observable<CompletionEvent> readOnlyStream;
    private PublishSubject<CompletionEvent> writeOnlyStream;

    public ServiceCompletionStream() {
        this.writeOnlyStream = PublishSubject.create();
        this.readOnlyStream =
            writeOnlyStream.onBackpressureBuffer(10000, null, BackpressureOverflow.ON_OVERFLOW_DROP_LATEST).share();
    }

    @Override
    public Observable<CompletionEvent> observe() {
        return this.readOnlyStream;
    }

    @Override
    public void write(CompletionEvent event) {
        writeOnlyStream.onNext(event);

    }
}
