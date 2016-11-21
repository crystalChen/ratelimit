package com.lian.iprd.ratelimit.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;
import com.lian.iprd.ratelimit.aop.RateLimit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RateLimitTest {


	public static void main(String[] args) throws RuntimeException, InterruptedException{
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		DemoService demoService = (DemoService) ctx.getBean("demoService");
//		demoService.querySQL("ggg");
		int taskCount = 6;
		CountDownLatch startSignal = new CountDownLatch(1);
		CountDownLatch doneSignal = new CountDownLatch(taskCount);
		for (int i = 0; i < taskCount; ++i)
			new Thread(new Worker(startSignal, doneSignal, demoService)).start();
		startSignal.countDown();
		System.out.println("\n\n\n================    start   ==================");
		doneSignal.await();
		System.out.println("================    end    ===================");
	}

}
