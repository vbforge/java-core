package com.vbforge.practice.practice_9_creating_threads;

/**
 * Thread creation — plain subclass of Thread vs a generic subclass that
 * carries a typed payload.
 *
 * Moved on-the-fly from `generics_fundamentals/example_threads/*` — the
 * subject here is threading, not generics. The generic variant shows a
 * common idiom: a typed payload carried by the thread's field rather than
 * passing it through a closure.
 */
public class ThreadCreationDemo {

    public static void main(String[] args) throws InterruptedException {

        // ================== Scene 1: plain Thread subclass ==================
        System.out.println("=== Scene 1: plain Thread subclass ===");
        MyThread thread = new MyThread();
        thread.start();
        System.out.println("Main thread is running!");
        thread.join();

        // ================== Scene 2: generic Thread subclass with payload ==================
        System.out.println("\n=== Scene 2: generic Thread subclass ===");
        MyThreadGeneric<String> thread1 = new MyThreadGeneric<>("Hello");
        MyThreadGeneric<Integer> thread2 = new MyThreadGeneric<>(123);

        thread1.start();
        thread2.start();
        System.out.println("Main thread is running!");
        thread1.join();
        thread2.join();
    }

    // -------- Scene 1 --------
    static class MyThread extends Thread {
        @Override
        public void run() {
            // code to be executed in the thread
            System.out.println("Thread is running!");
        }
    }

    // -------- Scene 2 --------
    static class MyThreadGeneric<T> extends Thread {
        private final T value;

        public MyThreadGeneric(T value) {
            this.value = value;
        }

        @Override
        public void run() {
            // code to be executed in the thread
            System.out.println("Thread is running with value: " + value);
        }
    }
}