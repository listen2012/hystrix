package com.listen.hytrix.groupping;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;

@Component
public class SearchDataStreamGrouping extends Subscriber<List<SearchEvent>> {


  @Override
  public void onCompleted() {
    System.out.println("grouping done：" + Thread.currentThread().getId() + "--" + Thread.currentThread().getName());
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(List<SearchEvent> searchEvents) {
    System.out.println("three:" + Thread.currentThread().getName() + searchEvents.stream().map(SearchEvent::getWord)
        .collect(Collectors.joining("===")));
  }
}
