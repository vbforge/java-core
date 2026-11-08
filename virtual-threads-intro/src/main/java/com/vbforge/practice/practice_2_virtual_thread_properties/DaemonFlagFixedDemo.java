package com.vbforge.practice.practice_2_virtual_thread_properties;

/** Virtual threads are always daemon: setDaemon(true) is a no-op, setDaemon(false) throws. */
public class DaemonFlagFixedDemo {

    public static void main(String[] args) {
        Thread thread = Thread.ofVirtual().unstarted(() -> { });

        thread.setDaemon(true);
        System.out.println("setDaemon(true) ok, isDaemon=" + thread.isDaemon());

        try {
            thread.setDaemon(false);
        } catch (IllegalArgumentException e) {
            System.out.println("setDaemon(false) -> " + e.getClass().getSimpleName());
        }
        System.out.println("isDaemon still " + thread.isDaemon());
    }
}
