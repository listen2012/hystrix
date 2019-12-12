package com.listen.hytrix.rxjava.metrics;

import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public abstract class ServiceEventCenter<Event extends ServiceEvent, Sample extends MetricsSample> {

    protected final Observable<long[]> bucketedStream;
    private final Func1<Observable<Event>, Observable<long[]>> reduceBucketToSummary;

    protected ServiceEventCenter(final ServiceEventStream<Event> inputEventStream, final int bucketSizeInS) {

        this.reduceBucketToSummary = eventOb -> eventOb.reduce(getEmptyBucketSummary(), reduce());
        this.bucketedStream = Observable.defer(
            () -> inputEventStream.observe()
                .window(bucketSizeInS, TimeUnit.SECONDS)
//                .window(10)
                .flatMap(reduceBucketToSummary)
                .startWith(getEmptyBucketSummary()));
        bucketedStream.subscribe(l -> System.out.println("center:" + l[0] + ":" + l[1]));
    }



    abstract Observable<long[]> observe();

    abstract long[] getEmptyBucketSummary();

    abstract long[] getEmptyOutputValue();

    abstract Func2<long[], Event, long[]> reduce();

    abstract Sample metric(long[] l);

    protected Func2<long[], long[], long[]> mergePeriodBuckets() {
        return (long1, long2) -> {
            for (int i = 0; i < long1.length; i++) {
                long1[i] += long2[i];
            }
            return long1;
        };
    }
}
