package com.listen.hytrix.rxjava.metrics;

import java.text.DecimalFormat;

public class ServiceCompletionStreamMetrics extends ServiceStreamMetrics {

  public void doSendEvent(String serviceName, DefaultServiceEventType eventType) {
    CompletionEvent event = new CompletionEvent(eventType);
    sendEvent(serviceName, event);
  }

  public static ServiceCompletionStreamMetrics newEmpty(){
    return new ServiceCompletionStreamMetrics();
  }

  private ServiceCompletionStreamMetrics(){

  }

  public ServiceCompletionStreamMetrics(String serviceName, ServiceCompletionEventAcceptor acceptor,
      ServiceCompletionStream eventStream) {
    super(serviceName, acceptor, eventStream);
  }

  public ServiceCompletionStreamMetrics getInstance(String serviceName) {
    ServiceCompletionStream completionStream = new ServiceCompletionStream();
    ServiceCompletionEventAcceptor acceptor = new ServiceCompletionEventAcceptor(completionStream, 1);
    return new ServiceCompletionStreamMetrics(serviceName, acceptor, completionStream);
  }

  @Override
  public void next(long[] l) {
    System.out.println(this.serviceName + ":" + l[0] + "----" + l[1]);
    MetricsSample sample = acceptor.metric(l);
    DefaultEventConfig config = new DefaultEventConfig();
    if (sample.getFailureRate() > config.getFailureRate()) {
      DecimalFormat df = new DecimalFormat("0.00 ");
      System.out.println(
          "-------" + this.serviceName + ": warning failureRate:" + df.format(sample.getFailureRate()) + "----------");
    }
  }
}
