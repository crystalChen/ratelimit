package com.lian.iprd.ratelimit.exception;


public class RateLimitException extends RuntimeException {

    public RateLimitException(String msg) {
        super(msg);
    }
}

