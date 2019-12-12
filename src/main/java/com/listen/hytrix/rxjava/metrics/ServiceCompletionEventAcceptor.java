package com.listen.hytrix.rxjava.metrics;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;

public class ServiceCompletionEventAcceptor extends ServiceEventCenter<CompletionEvent, MetricsSample> {

  private final Observable<long[]> sourceStream;
  private EventConfiguration config;
  private BehaviorSubject<long[]> subject = BehaviorSubject.create();
  private Subscription subscription;
  private Subscription nextSubscription;
  private AtomicReference<Boolean> isOn = new AtomicReference<>(false);

  protected ServiceCompletionEventAcceptor(final ServiceEventStream<CompletionEvent> inputEventStream,
      final int bucketSizeInS) {
    super(inputEventStream, bucketSizeInS);
    this.config = new DefaultEventConfig();

    Func1<Observable<long[]>, Observable<long[]>> reduceWindowToSummary = o -> o
        .scan(getEmptyOutputValue(), mergePeriodBuckets()).skip(config.getTimePeriod());

    this.sourceStream = this.bucketedStream.window(config.getTimePeriod()).flatMap(reduceWindowToSummary)
        .startWith(getEmptyOutputValue()).onBackpressureDrop();
    sourceStream.subscribe(l -> System.out.println("acceptor:" + l[0] + ":" + l[1]));
  }

  @Override
  Observable<long[]> observe() {
    return this.sourceStream;
  }

  protected void subscribe(Action1<long[]> onNext) {
    if (isOn.compareAndSet(false, true)) {
      subscription = observe().subscribe(subject);
      nextSubscription = subject.subscribe(onNext);
    }
  }

  protected void unSubscribe() {
    if (isOn.compareAndSet(true, false)) {
      subscription.unsubscribe();
      nextSubscription.unsubscribe();
    }
  }

  @Override
  long[] getEmptyBucketSummary() {
    return new long[DefaultServiceEventType.values().length];
  }

  @Override
  long[] getEmptyOutputValue() {
    return new long[DefaultServiceEventType.values().length];
  }

  public BehaviorSubject<long[]> getSubject() {
    return subject;
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
