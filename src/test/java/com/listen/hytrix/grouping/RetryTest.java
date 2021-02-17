package com.listen.hytrix.grouping;

import com.listen.hytrix.DemoApplication;
import com.listen.hytrix.groupping.SearchDataStream;
import com.listen.hytrix.groupping.SearchDataStreamFilter;
import com.listen.hytrix.groupping.SearchEvent;
import com.listen.hytrix.rxjava.retry.RetryResource;
import io.netty.util.HashedWheelTimer;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.schedulers.Schedulers;

@SpringBootTest(classes = DemoApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class RetryTest {

  @Autowired
  private SearchDataStreamFilter dataStreamFilter;

  @Test
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


