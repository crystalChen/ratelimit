package com.lian.iprd.ratelimit.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.lian.iprd.ratelimit.aop.RateLimit;

@Component
public class DemoService {
	
	final private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	AtomicInteger counter = new AtomicInteger();
	
	@RateLimit(qps=1)
	public String querySQL(String name) {		
		LOGGER.info("=======     name={},counter={}   ========", name, counter.getAndIncrement());
//		if(1==1) throw new RuntimeException();
		return "good luck!";
	}

	public void testJson() {
		System.out.println("AAA,yes!!!");
	}
}
