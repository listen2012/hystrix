package com.listen.hytrix.rxjava.metrics;

import lombok.Data;

@Data
public class MetricsSample {

    private long totalCount;

    private long successCount;

    private long failureCount;

    private Double failureRate;
}
