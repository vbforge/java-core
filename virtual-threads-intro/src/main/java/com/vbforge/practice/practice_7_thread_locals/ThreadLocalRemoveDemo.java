package com.vbforge.practice.practice_7_thread_locals;

/**
 * remove() drops the thread's copy; the next get() runs the initial supplier again.
 * Keep thread-local values small: there is one copy per virtual thread and virtual threads are never reused.
 */
public class ThreadLocalRemoveDemo {

    private static final ThreadLocal<String> CONTEXT = ThreadLocal.withInitial(() -> "default");

    public static void main(String[] args) throws InterruptedException {
        String[] log = new String[3];

        Thread thread = Thread.ofVirtual().start(() -> {
            log[0] = CONTEXT.get();
            CONTEXT.set("request-42");
            log[1] = CONTEXT.get();
            CONTEXT.remove();
            log[2] = CONTEXT.get();
        });
        thread.join();

        System.out.println("initial      : " + log[0]);
        System.out.println("after set    : " + log[1]);
        System.out.println("after remove : " + log[2]);
    }
}
