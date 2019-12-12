package com.listen.hytrix.rxjava.metrics;

public class DefaultEventConfig implements EventConfiguration {

  @Override
  public int getTimePeriod() {
    return 3;
  }

  @Override
  public double getFailureRate() {
    return 0.2;
  }
}
