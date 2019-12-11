package com.listen.hytrix.rxjava.counter;

import rx.Observable;

public class DefaultServiceStream extends AbstractServiceCompletionCounter<DefaultEventConfig> {


  private EventConfiguration config;

  public DefaultServiceStream(ServiceCompletionStream inputEventStream) {
    super(inputEventStream);
    getResult();
  }


}
