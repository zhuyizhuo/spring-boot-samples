package com.github.zhuyizhuo.actuator.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrometheusCustomMonitor {

    @Autowired
    MeterRegistry registry;

    private Counter errorCount;
    private Counter total;
    private DistributionSummary sum;

    @PostConstruct
    private void init() {
        errorCount = registry.counter("zhuo_custom_error_count", "status", "error");
        total = registry.counter("zhuo_custom_total", "custom", "total");
        sum = registry.summary("zhuo_custom_sum", "custom", "sum");
        errorCount.increment();
        total.increment();
        sum.count();
    }

    public Counter getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Counter errorCount) {
        this.errorCount = errorCount;
    }

    public Counter getTotal() {
        return total;
    }

    public void setTotal(Counter total) {
        this.total = total;
    }

    public DistributionSummary getSum() {
        return sum;
    }

    public void setSum(DistributionSummary sum) {
        this.sum = sum;
    }
}
