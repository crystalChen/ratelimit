package com.lian.iprd.ratelimit.model;

public class RateMethod {

    private String methodName;
    private double qps = 1000.0;
    private long timeout = -1;
    private long warmupPeriod = -1;

    public long getWarmupPeriod() {
        return warmupPeriod;
    }

    public void setWarmupPeriod(long warmupPeriod) {
        this.warmupPeriod = warmupPeriod;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public double getQps() {
        return qps;
    }

    public void setQps(double qps) {
        this.qps = qps;
    }

}
