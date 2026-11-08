package com.vbforge.practice.practice_1_creating_virtual_threads;

/**
 * Thread.startVirtualThread(Runnable): the shortest way to start one.
 * The thread has an empty name because no builder was involved.
 */
public class StartVirtualThreadShortcutDemo {

    public static void main(String[] args) throws InterruptedException {
        String[] message = new String[1];

        Thread thread = Thread.startVirtualThread(() ->
                message[0] = "hello from a " + (Thread.currentThread().isVirtual() ? "virtual" : "platform") + " thread");

        thread.join(); // join also makes the write to message[0] visible here

        System.out.println(message[0]);
        System.out.println("name is empty: " + thread.getName().isEmpty());
    }
}
