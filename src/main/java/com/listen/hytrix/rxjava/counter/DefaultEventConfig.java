package com.listen.hytrix.rxjava.counter;

public class DefaultEventConfig implements EventConfiguration {

  @Override
  public int getTimePeriod() {
    return 10;
  }

  @Override
  public double getFailureRate() {
    return 0.2;
  }
}
