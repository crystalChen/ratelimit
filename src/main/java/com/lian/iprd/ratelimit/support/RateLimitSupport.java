package com.lian.iprd.ratelimit.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.RateLimiter;

@Component
public class RateLimitSupport {
	
    private final ConcurrentMap<String, RateLimiter> rateLimits = new ConcurrentHashMap<String, RateLimiter>();

    public RateLimiter loadRateLimiter(String key, double qps, long warmupPeriod) {
    	RateLimiter rl = rateLimits.get(key);
    	if (null == rl) {
            if(warmupPeriod < 0.0)
			    rateLimits.putIfAbsent(key, RateLimiter.create(qps));
            else
                rateLimits.putIfAbsent(key, RateLimiter.create(qps, warmupPeriod, TimeUnit.MILLISECONDS));
			rl = rateLimits.get(key);
		}
    	return rl;
    }

    public RateLimiter getRateLimiter(String key) {
        return rateLimits.get(key);
    }

    /**
     * 判断是否能在timeout内获取permits个许可。若能，则阻塞等待；反之，则直接返回false(不阻塞等待)
     * @param key 限流的key
     * @param qps 每秒请求数
     * @param permits 要获取许可的数目
     * @param timeout 允许阻塞的最大时间
     * @param timeUnit 阻塞的时间单位
     * @return 是否能在timeout内获得permit
     */
    public boolean tryAcquire(String key, double qps, long warmupPeriod, int permits, long timeout, TimeUnit timeUnit) {
		return loadRateLimiter(key, qps, warmupPeriod).tryAcquire(permits, timeout, timeUnit);
    }

    /**
     * 从RateLimiter获取permits个许可，该方法会被阻塞直到获取到请求
     * @param key 限流的key
     * @param qps 每秒请求数
     * @param permits 要获取许可的数目
     * @return 阻塞的时间
     */
    public double acquire(String key, double qps, long warmupPeriod, int permits) {
    	return loadRateLimiter(key, qps, warmupPeriod).acquire(permits);
    }

    /**
     * 重置key的QPS
     * @param key 限流的key
     * @param qps 新QPS
     */
    public void setRate(String key, double qps) {
        //TODO 通过心跳连接DB刷新
    	RateLimiter rl = rateLimits.get(key);
        if(null != rl)
            rl.setRate(qps);
    }
}
