package com.listen.hytrix.rxjava.metrics;

import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public abstract class AbstractServiceEventAcceptor<Event extends ServiceEvent, Sample extends MetricsSample>
    extends ServiceEventCenter {

    private final Observable<long[]> sourceStream;
    private BehaviorSubject<long[]> subject = BehaviorSubject.create();
    private Subscription candidateSubscription;
    private AtomicReference<Boolean> isOn = new AtomicReference<>(false);

    protected AbstractServiceEventAcceptor(ServiceEventStream<Event> inputEventStream, int bucketSizeInS,
        EventConfiguration config) {
        super(inputEventStream, bucketSizeInS);
        Func1<Observable<long[]>, Observable<long[]>> reduceWindowToSummary =
            o -> o.scan(getEmptyOutputValue(), mergePeriodBuckets()).skip(config.getTimePeriod());

        this.sourceStream = this.bucketedStream
            .window(config.getTimePeriod())
            .flatMap(reduceWindowToSummary)
            .startWith(getEmptyOutputValue())
            .share()
            .onBackpressureDrop();
        sourceStream.subscribe(l -> System.out.println("acceptor:" + l[0] + ":" + l[1]));
    }

    @Override
    Observable<long[]> observe() {
        return this.sourceStream;
    }

    protected void subscribe(Action1<long[]> onNext) {
        if (isOn.compareAndSet(false, true)) {
            candidateSubscription = observe().subscribe(subject);
            subject.subscribe(onNext, Throwable::printStackTrace);
        }
    }

    protected void unSubscribe() {
        if (isOn.compareAndSet(true, false)) {
            candidateSubscription.unsubscribe();
        }
    }
}
