package com.lian.iprd.ratelimit.aop;

import org.aspectj.lang.ProceedingJoinPoint;

public abstract class RateLimitAspectJ {

	public abstract  Object around(ProceedingJoinPoint pjp) throws Throwable;

}
