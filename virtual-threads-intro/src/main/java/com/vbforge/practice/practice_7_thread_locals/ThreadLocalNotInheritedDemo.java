package com.vbforge.practice.practice_7_thread_locals;

/** A plain ThreadLocal is not inherited: a new virtual thread starts with the initial value, not its parent's. */
public class ThreadLocalNotInheritedDemo {

    private static final ThreadLocal<String> USER = ThreadLocal.withInitial(() -> "anonymous");

    public static void main(String[] args) throws InterruptedException {
        USER.set("alice");
        System.out.println("main  sees: " + USER.get());

        String[] seen = new String[2];
        Thread parent = Thread.ofVirtual().start(() -> {
            seen[0] = USER.get();
            USER.set("bob");
            Thread child = Thread.ofVirtual().start(() -> seen[1] = USER.get());
            try {
                child.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        parent.join();

        System.out.println("child of main sees: " + seen[0]);
        System.out.println("child of a thread that set \"bob\" sees: " + seen[1]);
        System.out.println("main  still sees: " + USER.get());
    }
}
