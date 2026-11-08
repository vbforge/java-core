package com.vbforge.practice.practice_1_creating_virtual_threads;

/**
 * Thread.ofVirtual() builder: name the thread, start it, always join.
 * Virtual threads are daemon threads, so without join() the JVM may exit first.
 */
public class VirtualThreadBuilderDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = Thread.ofVirtual()
                .name("builder-thread")
                .start(() -> System.out.println("inside : name=" + Thread.currentThread().getName()
                        + ", virtual=" + Thread.currentThread().isVirtual()));

        thread.join();

        System.out.println("outside: name=" + thread.getName() + ", virtual=" + thread.isVirtual());
    }
}
