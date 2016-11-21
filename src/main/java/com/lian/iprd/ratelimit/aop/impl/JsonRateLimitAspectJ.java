package com.lian.iprd.ratelimit.aop.impl;


import com.google.common.util.concurrent.RateLimiter;
import com.lian.iprd.ratelimit.aop.RateLimitAspectJ;
import com.lian.iprd.ratelimit.exception.RateLimitException;
import com.lian.iprd.ratelimit.model.RateClass;
import com.lian.iprd.ratelimit.model.RateMethod;
import com.lian.iprd.ratelimit.support.JsonConfig;
import com.lian.iprd.ratelimit.support.RateLimitSupport;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class JsonRateLimitAspectJ extends RateLimitAspectJ implements InitializingBean {

    @Resource
    private RateLimitSupport rateLimitSupport;

    private Map<String, RateClass> jsonCfgMap;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 初始化json限流配置
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        jsonCfgMap =  JsonConfig.loadRateClassMap();
        for (RateClass rc : jsonCfgMap.values()) {
            if(StringUtils.isNotBlank(rc.getClassName()) && rc.getQps()!=null )
                rateLimitSupport.loadRateLimiter(rc.getClassName(),rc.getQps(), -1);
            Map<String, RateMethod>  methodMap = rc.getMethods();
            for (RateMethod rm : methodMap.values()) {
                if (StringUtils.isNotBlank(rm.getMethodName()))
                    rateLimitSupport.loadRateLimiter(rm.getMethodName(),rm.getQps(),rm.getWarmupPeriod());
            }
        }
    }

    @Override
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String clazz = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        String fullMethodName = clazz + "." + methodName;
        RateLimiter clazzRL = rateLimitSupport.getRateLimiter(clazz);
        if (null != clazzRL) {
            double waitTime = clazzRL.acquire();
            LOGGER.info("key={},sleepTime={}", clazz, waitTime);
        }

        RateLimiter methodRL = rateLimitSupport.getRateLimiter(fullMethodName);
        if (null != methodRL) {
            long timeout = jsonCfgMap.get(clazz).getMethods().get(methodName).getTimeout();
            if (timeout > 0) {
                Boolean permit = methodRL.tryAcquire(1, timeout, TimeUnit.MILLISECONDS);
                if (!permit) {
                    LOGGER.info("key={}, timeout={}, permit={}", fullMethodName, timeout, permit.toString());
                    throw new RateLimitException("reject the request that QPS is too high, can not get the token.");
                }
            } else {
                double waitTime = methodRL.acquire();
                LOGGER.info("key={},sleepTime={}", clazz, waitTime);
            }
        }
        try {
            Object result = pjp.proceed();
            return result;
        } catch (Throwable t) {
            throw t;
        }
    }

}
