package com.vbforge.practice.practice_2_virtual_thread_properties;

/** The priority of a virtual thread is always NORM_PRIORITY; setPriority is silently ignored. */
public class FixedPriorityDemo {

    public static void main(String[] args) {
        Thread thread = Thread.ofVirtual().unstarted(() -> { });

        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println("after setPriority(MAX): " + thread.getPriority());

        thread.setPriority(Thread.MIN_PRIORITY);
        System.out.println("after setPriority(MIN): " + thread.getPriority());
    }
}
