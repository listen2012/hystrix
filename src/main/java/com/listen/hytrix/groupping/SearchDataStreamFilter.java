package com.listen.hytrix.groupping;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

@Component
public class SearchDataStreamFilter extends Subscriber<List<SearchEvent>> {

  @Autowired
  private SearchDataStreamGrouping dataStreamGrouping;

  private ThreadLocal<BehaviorSubject<SearchEvent>> subjects = ThreadLocal.withInitial(() -> BehaviorSubject.create());
  private ThreadLocal<AtomicBoolean> isOn = ThreadLocal.withInitial(() -> new AtomicBoolean(false));


  public List<SearchEvent> filter(List<SearchEvent> source) {
    return source.stream().filter(s -> s.getWord().endsWith("2")).collect(Collectors.toList());
  }

  @Override
  public void onCompleted() {
    subjects.get().onCompleted();
    subjects.remove();
    isOn.remove();
    System.out.println("filter done：" + Thread.currentThread().getId() + "--" + Thread.currentThread().getName());
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(List<SearchEvent> searchEvents) {
    List<SearchEvent> filteredStream = filter(searchEvents);
    System.out.println(
        "two:" + Thread.currentThread().getId() + "--" + Thread.currentThread().getName() + filteredStream.stream()
            .map(SearchEvent::getWord)
            .collect(Collectors.joining(",")));
    if (!isOn.get().get()) {
      subjects.get().buffer(500, TimeUnit.MILLISECONDS, 2)
          .filter(l -> !CollectionUtils.isEmpty(l)).subscribe(dataStreamGrouping);
      isOn.get().set(true);
    }
    for (SearchEvent event : filteredStream) {
      subjects.get().onNext(event);
    }
  }
}
