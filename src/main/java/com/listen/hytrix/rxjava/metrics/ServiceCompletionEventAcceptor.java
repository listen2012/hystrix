package com.listen.hytrix.rxjava.metrics;

import java.util.stream.LongStream;
import rx.functions.Func2;

public class ServiceCompletionEventAcceptor extends AbstractServiceEventAcceptor<CompletionEvent, MetricsSample> {


    protected ServiceCompletionEventAcceptor(final ServiceEventStream<CompletionEvent> inputEventStream,
        final int bucketSizeInS) {
        super(inputEventStream, bucketSizeInS, new DefaultEventConfig());
    }

    @Override
    long[] getEmptyBucketSummary() {
        return new long[DefaultServiceEventType.values().length];
    }

    @Override
    long[] getEmptyOutputValue() {
        return new long[DefaultServiceEventType.values().length];
    }

    @Override
    Func2<long[], CompletionEvent, long[]> reduce() {
        return (l, event) -> {
            l[event.getEventType().ordinal()] += 1;
            return l;
        };
    }

    @Override
    MetricsSample metric(long[] l) {
        MetricsSample sample = new MetricsSample();
        sample.setSuccessCount(l[DefaultServiceEventType.SUCCESS.ordinal()]);
        sample.setFailureCount(l[DefaultServiceEventType.FAIL.ordinal()]);
        sample.setTotalCount(LongStream.of(l).sum());
        sample.setFailureRate((double) sample.getFailureCount() / sample.getTotalCount());
        return sample;
    }

}
