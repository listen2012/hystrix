package com.listen.hytrix.rxjava.counter;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public abstract class AbstractServiceCompletionCounter<config extends EventConfiguration> extends
    ServiceEventCounter<DefaultServiceEventType> {

  private final Observable<long[]> sourceStream;

  private EventConfiguration config;


  protected AbstractServiceCompletionCounter(ServiceCompletionStream inputEventStream) {
    super(inputEventStream, 1);
    this.config = new DefaultEventConfig();
    Func1<Observable, Observable<long[]>> reduceWindowToSummary = (w) -> w.scan(getEmptyOutputValue(), reduce())
        .skip(config.getTimePeriod());
    this.sourceStream = bucketedStream.window(config.getTimePeriod(), 1).flatMap(reduceWindowToSummary).share()
        .onBackpressureDrop();
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
  Func2<long[], DefaultServiceEventType, long[]> reduce() {
    return (l, event) -> {
      l[event.ordinal()] += 1;
      return l;
    };
  }

  public Observable<long[]> observe() {
    return this.sourceStream;
  }
}
