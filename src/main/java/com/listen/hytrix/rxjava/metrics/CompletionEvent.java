package com.listen.hytrix.rxjava.metrics;

import lombok.Data;

@Data
public class CompletionEvent implements ServiceEvent {

    private DefaultServiceEventType eventType;

    public CompletionEvent(DefaultServiceEventType eventType) {
        this.eventType = eventType;
    }
}
