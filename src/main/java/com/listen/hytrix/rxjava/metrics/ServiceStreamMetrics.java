package com.listen.hytrix.rxjava.metrics;


import java.time.LocalDateTime;
import lombok.Data;
import rx.functions.Action1;

@Data
public abstract class ServiceStreamMetrics {

  /**
   * 服务名
   */
  protected String serviceName;

  /**
   * 事件接受类
   */
  protected AbstractServiceEventAcceptor acceptor;

  /**
   * 服务事件流
   */
  protected ServiceEventStream eventStream;

  protected LocalDateTime updateTime;

  protected Action1<long[]> action = this::next;


  protected ServiceStreamMetrics(){}

  protected ServiceStreamMetrics(String serviceName, AbstractServiceEventAcceptor acceptor,
      ServiceEventStream eventStream) {
    this.serviceName = serviceName;
    this.acceptor = acceptor;
    this.eventStream = eventStream;
  }

  public void sendEvent(String serviceName, ServiceEvent event) {
    if (!ServiceEventChannelHandler.initialized(serviceName)) {
      ServiceEventChannelHandler.generateChannel(serviceName, getInstance(serviceName));
    }
    ServiceEventChannelHandler.sendEvent(serviceName, event);
  }

  public abstract ServiceStreamMetrics getInstance(String serviceName);


  /**
   * 对统计结果处理方法，具体告警方式在子类实现
   *
   * @param l 服务单位时间统计结果
   */

  public void next(long[] l) {
//    Logger.info("serviceRadar", Joiner.on(":").join(serviceName, l));
  }
}
