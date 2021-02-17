package com.listen.hytrix.groupping;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rx.Subscription;
import rx.schedulers.Schedulers;

@Service
public class DataProducer {


  @Autowired
  private SearchDataStreamFilter dataStreamFilter;

  public void test() throws InterruptedException {
    for (int j = 0; j < 4; j++) {
      new Thread(() ->
      {
        SearchDataStream stream = new SearchDataStream();
        Subscription subscription = stream.observe()
            .buffer(50, TimeUnit.MILLISECONDS, 10)
            .filter(l -> !CollectionUtils.isEmpty(l))
            .observeOn(Schedulers.newThread())
            .subscribe(dataStreamFilter);
        for (int i = 0; i < 50; i++) {
          stream.write(new SearchEvent(String.valueOf(i), Thread.currentThread().getName() + "-" + i));
        }
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        stream.getWriteOnlyStream().onCompleted();
      }).start();
    }
    Thread.sleep(100000L);
  }
}
