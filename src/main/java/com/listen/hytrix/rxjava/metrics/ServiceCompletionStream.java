package com.listen.hytrix.rxjava.metrics;

import rx.Observable;
import rx.subjects.PublishSubject;

public class ServiceCompletionStream implements ServiceEventStream<CompletionEvent> {

    private Observable<CompletionEvent> readOnlyStream;
    private PublishSubject<CompletionEvent> writeOnlyStream;

    public ServiceCompletionStream() {
        this.writeOnlyStream = PublishSubject.create();
        this.readOnlyStream = writeOnlyStream.share();
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
