package com.lian.iprd.ratelimit.aop.impl;


import com.lian.iprd.ratelimit.aop.RateLimit;
import com.lian.iprd.ratelimit.aop.RateLimitAspectJ;
import com.lian.iprd.ratelimit.exception.RateLimitException;
import com.lian.iprd.ratelimit.support.RateLimitSupport;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@Component
public class AnnotationRateLimitAspectJ extends RateLimitAspectJ{

    @Resource
    private RateLimitSupport rateLimitSupport;

    private static final String executionURL = "@annotation(com.lian.iprd.ratelimit.aop.RateLimit)";

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Around(executionURL)
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String key = this.generateKey(pjp);
        RateLimit rateLimit = ((MethodSignature) pjp.getSignature()).getMethod()
                .getAnnotation(RateLimit.class);
        double qps = rateLimit.qps();
        long timeout = rateLimit.timeout();
        long warmupPeriod = rateLimit.warmupPeriod();
        if (timeout < 0) {
            double waitTime = rateLimitSupport.acquire(key, qps, warmupPeriod, 1);
            LOGGER.info("key={},sleepTime={}", key, waitTime);
        } else {
            Boolean permit = rateLimitSupport.tryAcquire(key, qps, warmupPeriod, 1, timeout, TimeUnit.MILLISECONDS);
            LOGGER.info("key={},qps={},timeout={},permit={}", key,qps,timeout,permit.toString());
            if(!permit)
                throw new RateLimitException("reject the request that QPS is too high, can not get the token.");
        }
        try {
            Object result = pjp.proceed();
            return result;
        } catch (Throwable t) {
            throw t;
        }

    }

    /**
     * 生成限流的key。用className、methodName和args长度作为key的因子
     */
    private String generateKey(ProceedingJoinPoint pjp) {
        String clazz = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        int length = 0;
        if(null != args) {
            length = args.length;
        }
        String key = clazz + "." + methodName + '.' + length;
        return key;
    }


}