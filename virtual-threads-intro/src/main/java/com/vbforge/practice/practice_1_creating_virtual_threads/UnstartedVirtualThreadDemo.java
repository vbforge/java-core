package com.vbforge.practice.practice_1_creating_virtual_threads;

/**
 * unstarted(): create a virtual thread without running it. State NEW until start();
 * a thread can be started only once.
 */
public class UnstartedVirtualThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = Thread.ofVirtual()
                .name("not-yet")
                .unstarted(() -> System.out.println("running: " + Thread.currentThread().getName()));

        System.out.println("before start: " + thread.getState());

        thread.start();
        thread.join();
        System.out.println("after join  : " + thread.getState());

        try {
            thread.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("second start() -> " + e.getClass().getSimpleName());
        }
    }
}
