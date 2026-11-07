package com.vbforge.practice.practice_8_synchronized_map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Logging the access time of users into a synchronizedMap.
 * A single put needs no extra lock (the wrapper locks each call),
 * but iterating over the map (forEach / entrySet) must hold the map's lock.
 */
public class SynchronizedUserAccessLogDemo {

    private final Map<String, Long> accessLog = Collections.synchronizedMap(new HashMap<>());

    public void logAccess(String userId) {
        accessLog.put(userId, System.currentTimeMillis());
    }

    public void printAccessLog() {
        // sorted copy + "has a timestamp" instead of the raw millis, so the output is stable between runs
        Map<String, Boolean> view = new TreeMap<>();
        synchronized (accessLog) {
            accessLog.forEach((user, time) -> view.put(user, time > 0));
        }
        view.forEach((user, hasTime) -> System.out.println(user + " logged an access time: " + hasTime));
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedUserAccessLogDemo log = new SynchronizedUserAccessLogDemo();

        Runnable task = () -> log.logAccess(Thread.currentThread().getName());

        Thread t1 = new Thread(task, "user-A");
        Thread t2 = new Thread(task, "user-B");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log.printAccessLog();
    }
}
