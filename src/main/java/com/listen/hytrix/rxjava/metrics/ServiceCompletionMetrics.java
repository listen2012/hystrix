package com.listen.hytrix.rxjava.metrics;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.Data;
import rx.functions.Action1;

public class ServiceCompletionMetrics {

  private static ConcurrentMap<String, ServiceStreamPack> streamPool = new ConcurrentHashMap<>(128);

  private static void buildChannel(String serviceName) {
    if (!streamPool.containsKey(serviceName)) {
      ServiceCompletionStream completionStream = new ServiceCompletionStream();
      ServiceCompletionEventAcceptor acceptor = new ServiceCompletionEventAcceptor(completionStream, 1);
      ServiceStreamPack pack = new ServiceStreamPack(serviceName, acceptor, completionStream);
      streamPool.putIfAbsent(serviceName, pack);
      acceptor.subscribe(pack.getAction());
    }
  }

  private static ServiceEventStream getStream(String serviceName) {
    if (!streamPool.containsKey(serviceName)) {
      buildChannel(serviceName);
    }
    return streamPool.get(serviceName).getEventStream();

  }

  public static void sendEvent(String serviceName, DefaultServiceEventType eventType) {
    CompletionEvent event = new CompletionEvent(eventType);
    getStream(serviceName).write(event);
  }

  @Data
  static class ServiceStreamPack {

    private String serviceName;

    private ServiceEventCenter acceptor;

    private ServiceEventStream eventStream;

    private Action1<long[]> action = l -> next(l);

    public ServiceStreamPack(String serviceName, ServiceEventCenter acceptor, ServiceEventStream eventStream) {
      this.serviceName = serviceName;
      this.acceptor = acceptor;
      this.eventStream = eventStream;
    }

    private void next(long[] l) {
      System.out.println(this.serviceName + ":" + l[0] + "----" + l[1]);
      MetricsSample sample = acceptor.metric(l);
      DefaultEventConfig config = new DefaultEventConfig();
      if (sample.getFailureRate() > config.getFailureRate()) {
        DecimalFormat df = new DecimalFormat("0.00 ");
        System.out.println(
            this.serviceName + "-------warning failureRate:" + df.format(sample.getFailureRate()) + "----------");
      }
    }

  }
}
