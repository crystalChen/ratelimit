package com.lian.iprd.ratelimit.test;


public class LeakyBucket {
    double rate;               // leak rate in calls/s
    double burst;              // bucket size
    long lastRefreshTime;          // time for last water refresh
    double water;              // water count at refreshTime

    public void refreshWater() {
        long  now = System.currentTimeMillis()/1000;
        //how time flies.
        water = Math.max( 0, water - (now - lastRefreshTime)*rate );
        lastRefreshTime = now;
    }

    public boolean permissionGranted() {
        refreshWater();
        if (water < burst) { // bucket not overflow.
            water ++;
            return true;
        } else {
            return false;
        }
    }
}



/*
        listLength = LLEN rate.limiting:IP
        if listLength < 10
            LPUSH rate.limiting:IP, now()
        else
            time = LINDEX rate.limiting:IP, -1
        if now() - time < 60
            ERROR "qps to high, try again later plz"
        else
        LPUSH rate.limiting:IP, now()
        LTRIM rate.limiting:IP, 0, 9
*/
