package com.listen.hytrix.rxjava.metrics;


import rx.Observable;

public interface ServiceEventStream<Event extends ServiceEvent> {

    Observable<Event> observe();

    void write(Event e);
}
