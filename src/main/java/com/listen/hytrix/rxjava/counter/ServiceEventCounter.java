package com.listen.hytrix.rxjava.counter;

import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;

public abstract class ServiceEventCounter<Event> {

  protected final Observable<long[]> bucketedStream;
  private final Func1<Observable<Event>, Observable<long[]>> reduceBucketToSummary;
  private BehaviorSubject<long[]> subject = BehaviorSubject.create(getEmptyOutputValue());

  protected ServiceEventCounter(final ServiceCompletionStream inputEventStream, final int bucketSizeInMs) {

    this.reduceBucketToSummary = new Func1<Observable<Event>, Observable<long[]>>() {
      @Override
      public Observable<long[]> call(Observable<Event> eventBucket) {
        return eventBucket.reduce(getEmptyBucketSummary(), reduce());
      }
    };

    this.bucketedStream = Observable.defer(
        () -> inputEventStream.observe().window(bucketSizeInMs, TimeUnit.SECONDS).flatMap(reduceBucketToSummary)
            .startWith(getEmptyBucketSummary()));
  }

  public long[] getResult() {
    Subscription candidateSubscription = observe().subscribe(subject);
    if (subject.hasValue()) {
      return subject.getValue();

    }
    return getEmptyOutputValue();
  }

  abstract Observable<long[]> observe();

  abstract long[] getEmptyBucketSummary();

  abstract long[] getEmptyOutputValue();


  abstract Func2<long[], Event, long[]> reduce();
}
