package com.listen.hytrix.rxjava.metrics;

import io.netty.util.concurrent.DefaultThreadFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StringUtils;

public class ServiceEventChannelHandler {

  private static ConcurrentMap<String, ServiceStreamMetrics> streamPool = new ConcurrentHashMap<>(128);
  private static ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1,
      new DefaultThreadFactory("eventChannelPool"));

  static {
    scheduler.scheduleAtFixedRate(ServiceEventChannelHandler::scanPool, 30, 60, TimeUnit.MINUTES);
  }

  /**
   * 服务统计通道初始化
   */
  public static void generateChannel(String service, ServiceStreamMetrics pack) {
    if (pack == null || pack.getAcceptor() == null || pack.getEventStream() == null || StringUtils.isEmpty(service)) {
      return;
    }
    buildChannel(service, pack);
  }

  public static boolean initialized(String serviceName) {
    return streamPool.containsKey(serviceName);
  }

  /**
   * 发送服务事件，若通道不存在将按默认设置初始化
   */
  public static void sendEvent(String serviceName, ServiceEvent event) {
    if (StringUtils.isEmpty(serviceName)) {
      return;
    }
    getStream(serviceName).write(event);
  }

  private static void scanPool() {
    if (!streamPool.isEmpty()) {
      for (Entry<String, ServiceStreamMetrics> e : streamPool.entrySet()) {
        if (Duration.between(e.getValue().getUpdateTime(), LocalDateTime.now()).toMinutes() >= 30) {
          streamPool.remove(e.getKey());
        }
      }
    }
  }

  private static void buildChannel(String serviceName, ServiceStreamMetrics pack) {
    if (!streamPool.containsKey(serviceName)) {
      pack.setUpdateTime(LocalDateTime.now());
      streamPool.putIfAbsent(serviceName, pack);
      pack.getAcceptor().subscribe(pack.getAction());
    }
  }

  private static ServiceEventStream<ServiceEvent> getStream(String serviceName) {
    if (!streamPool.containsKey(serviceName)) {
      ServiceCompletionStream completionStream = new ServiceCompletionStream();
      ServiceCompletionEventAcceptor acceptor = new ServiceCompletionEventAcceptor(completionStream, 1);
      ServiceCompletionStreamMetrics pack = new ServiceCompletionStreamMetrics(serviceName, acceptor, completionStream);
      buildChannel(serviceName, pack);
    }
    streamPool.get(serviceName).setUpdateTime(LocalDateTime.now());
    return streamPool.get(serviceName).getEventStream();

  }
}
