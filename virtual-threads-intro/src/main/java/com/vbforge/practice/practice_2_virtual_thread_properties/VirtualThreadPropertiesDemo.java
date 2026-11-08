package com.vbforge.practice.practice_2_virtual_thread_properties;

/**
 * Properties of a fresh virtual thread: virtual, daemon, NORM_PRIORITY, empty name,
 * the shared "VirtualThreads" group. Read before start(): a terminated thread has no group.
 */
public class VirtualThreadPropertiesDemo {

    public static void main(String[] args) {
        Thread thread = Thread.ofVirtual().unstarted(() -> { });

        System.out.println("isVirtual : " + thread.isVirtual());
        System.out.println("isDaemon  : " + thread.isDaemon());
        System.out.println("priority  : " + thread.getPriority() + " (NORM_PRIORITY=" + Thread.NORM_PRIORITY + ")");
        System.out.println("name empty: " + thread.getName().isEmpty());
        System.out.println("group     : " + thread.getThreadGroup().getName());
    }
}
