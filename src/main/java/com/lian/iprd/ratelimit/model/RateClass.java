package com.lian.iprd.ratelimit.model;

import java.util.HashMap;
import java.util.Map;

public class RateClass {

    private String className;
    private Double qps;
    private long timeout;
    private long warmupPeriod;
    private Map<String, RateMethod> methods = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getQps() {
        return qps;
    }

    public void setQps(Double qps) {
        this.qps = qps;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getWarmupPeriod() {
        return warmupPeriod;
    }

    public void setWarmupPeriod(long warmupPeriod) {
        this.warmupPeriod = warmupPeriod;
    }

    public Map<String, RateMethod> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, RateMethod> methods) {
        this.methods = methods;
    }
}
