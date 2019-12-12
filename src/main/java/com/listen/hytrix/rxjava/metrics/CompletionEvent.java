package com.listen.hytrix.rxjava.metrics;

import lombok.Data;

@Data
public class CompletionEvent implements ServiceEvent {

    private com.listen.hytrix.rxjava.metrics.DefaultServiceEventType eventType;

    public CompletionEvent(com.listen.hytrix.rxjava.metrics.DefaultServiceEventType eventType) {
        this.eventType = eventType;
    }
}
