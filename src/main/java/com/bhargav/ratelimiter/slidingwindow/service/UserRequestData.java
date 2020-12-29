package com.bhargav.ratelimiter.slidingwindow.service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class UserRequestData {

    private int requestLimit;
    private int windowTimeInSec;
    private Queue<Long> requestTimeStamps;

    public UserRequestData(int requestLimit, int windowTimeInSec) {
        this.requestLimit = requestLimit;
        this.windowTimeInSec = windowTimeInSec;
        this.requestTimeStamps = new ConcurrentLinkedDeque<Long>();
    }

    public int getRemainingRequests() {
        return requestLimit - requestTimeStamps.size();
    }

    public int getRequestWaitTime() {
        long currentTimeStamp = System.currentTimeMillis() / 1000;
        int initialElapsedTime = (int) (currentTimeStamp - requestTimeStamps.peek());
        return initialElapsedTime > windowTimeInSec ? 0 : windowTimeInSec - initialElapsedTime;
    }

    public boolean isServiceCallAllowed() {
        long currentTimeStamp = System.currentTimeMillis() / 1000;
        evictOlderRequestTimeStamps(currentTimeStamp);

        if (requestTimeStamps.size() >= this.requestLimit) {
            return false;
        }

        requestTimeStamps.add(currentTimeStamp);
        return true;
    }

    public void evictOlderRequestTimeStamps(long currentTimeStamp) {
        while (requestTimeStamps.size() != 0 &&
                (currentTimeStamp - requestTimeStamps.peek() > windowTimeInSec)) {
            requestTimeStamps.remove();
        }
    }

}
