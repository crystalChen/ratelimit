package com.lian.iprd.ratelimit.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {

	double qps();

	/**
	 * 以判断能否在timeout内获得token的方式获取许可。若能则最多阻塞timeout，不能直接返回。
	 * 负数标志不使用timeout方式，而是默认阻塞等待许。
	 * timeout单位是毫秒(1秒=1000毫秒)
	 */
	long timeout() default 0;

	/**
	 * 以预热形式限流。在预热时间内，每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率。
	 * 默认负数不预热
	 * warmupPeriod单位是毫秒(1秒=1000毫秒)
	 */
	long warmupPeriod() default -1;

//	/**
//	 * timeout的时间单位
//	 */
//	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
