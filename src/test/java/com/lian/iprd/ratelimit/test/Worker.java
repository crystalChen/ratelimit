package com.lian.iprd.ratelimit.test;

import java.util.concurrent.CountDownLatch;

import javax.annotation.Resource;

public class Worker implements Runnable {

	DemoService demoService;
	private final CountDownLatch startSignal;
	private final CountDownLatch doneSignal;

	public Worker(CountDownLatch startSignal, CountDownLatch doneSignal, DemoService demoService) {
		this.startSignal = startSignal;
		this.doneSignal = doneSignal;
		this.demoService = demoService;
	}

	public void run() {
		try {
			startSignal.await();
			doWork();
			doneSignal.countDown();
		} catch (InterruptedException ex) {
			System.out.println("===== Worker InterruptedException  =====");
		}
	}

	void doWork() {
		for (int i = 0; i < 1; i++) {
			demoService.querySQL("Julia");
		}
	}

}