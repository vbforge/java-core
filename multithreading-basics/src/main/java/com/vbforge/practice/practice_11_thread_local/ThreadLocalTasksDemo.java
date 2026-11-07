package com.vbforge.practice.practice_11_thread_local;

public class ThreadLocalTasksDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<Integer> value = ThreadLocal.withInitial(() -> 0);

        Task1 task1 = new Task1(value);
        Task2 task2 = new Task2(value);

        task1.start();
        task2.start();

        // Let the threads run for 2 seconds
        Thread.sleep(2000);

        // Stop the threads gracefully
        task1.stopThread();
        task2.stopThread();

        // Wait for threads to finish
        task1.join();
        task2.join();

        System.out.println("Threads stopped gracefully.");
    }

    static class Task1 extends Thread {
        private final ThreadLocal<Integer> value;
        private volatile boolean running = true; // Flag to control thread execution

        public Task1(ThreadLocal<Integer> value) {
            this.value = value;
        }

        public void stopThread() {
            running = false; // Method to stop the thread
        }

        @Override
        public void run() {
            int iterations = 0;
            while (running && iterations < 10) { // Limit iterations to avoid infinite loop
                int result = value.get() != null ? value.get() : 0; // Handle null initial value
                value.set(result + 1);
                System.out.println("Task1: " + value.get());
                iterations++;
                try {
                    Thread.sleep(100); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Task1 interrupted");
                    break;
                }
            }
        }
    }

    static class Task2 extends Thread {
        private final ThreadLocal<Integer> value;
        private volatile boolean running = true; // Flag to control thread execution

        public Task2(ThreadLocal<Integer> value) {
            this.value = value;
        }

        public void stopThread() {
            running = false; // Method to stop the thread
        }

        @Override
        public void run() {
            int iterations = 0;
            while (running && iterations < 10) { // Limit iterations to avoid infinite loop
                int result = value.get() != null ? value.get() : 0; // Handle null initial value
                value.set(result - 1);
                System.out.println("Task2: " + value.get());
                iterations++;
                try {
                    Thread.sleep(100); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Task2 interrupted");
                    break;
                }
            }
        }
    }
}
