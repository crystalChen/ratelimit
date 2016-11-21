package com.lian.iprd.ratelimit.test;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenzhenqing on 2016/11/17.
 */
public class RateLimiterTest {

    public static void main(String[] s) throws InterruptedException {

        testBlockAcquire();
//        testTryAcquire();
//        testAcquireTooMuch();
//        testWarmup();
    }

    public static void testBlockAcquire() throws InterruptedException {
        RateLimiter rl = RateLimiter.create(1);
//        Thread.sleep(1000);
        for (int i = 0; i < 6; i++) {
            System.out.println(rl.acquire());
        }
    }

    public static void testTryAcquire() throws InterruptedException {
        RateLimiter rl = RateLimiter.create(1);
        for (int i = 0; i < 3; i++) {
            System.out.println(rl.acquire());
        }
//        Thread.sleep(1000);
        System.out.println(rl.tryAcquire());
    }

    public static void testAcquireTooMuch() {
        RateLimiter rl = RateLimiter.create(10);
        System.out.println(rl.acquire(100));
        System.out.println(rl.acquire());
    }

    public static void testWarmup() throws InterruptedException {
        RateLimiter rl = RateLimiter.create(10, 1, TimeUnit.SECONDS);
//        rl.acquire(5);
//        Thread.sleep(2000);
        for (int i = 0; i < 10; i++) {
            System.out.println(rl.acquire());
        }
        Thread.sleep(900);
        for (int i = 0; i < 10; i++) {
            System.out.println(rl.acquire());
        }
    }

}
