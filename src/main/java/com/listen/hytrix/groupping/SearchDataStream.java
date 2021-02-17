package com.listen.hytrix.groupping;

import com.listen.hytrix.rxjava.metrics.CompletionEvent;
import com.listen.hytrix.rxjava.metrics.ServiceEventStream;
import lombok.Data;
import rx.BackpressureOverflow;
import rx.Observable;
import rx.subjects.PublishSubject;

@Data
public class SearchDataStream  {

    private Observable<SearchEvent> readOnlyStream;
    private PublishSubject<SearchEvent> writeOnlyStream;

    public SearchDataStream() {
        this.writeOnlyStream = PublishSubject.create();
        this.readOnlyStream =
            writeOnlyStream.onBackpressureBuffer(10000, null, BackpressureOverflow.ON_OVERFLOW_DROP_LATEST).share();
    }

    public Observable<SearchEvent> observe() {
        return this.readOnlyStream;
    }

    public void write(SearchEvent event) {
        writeOnlyStream.onNext(event);

    }
}
