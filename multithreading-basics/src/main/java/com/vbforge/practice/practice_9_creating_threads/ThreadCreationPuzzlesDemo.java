package com.vbforge.practice.practice_9_creating_threads;

/**
 * Small "does it start a thread / what does it print" puzzles about thread creation.
 *
 * Scene 1 — Does the main() method start a new thread?  Yes. + No.
 * Scene 2 — Anonymous Thread subclass overriding run() vs. the Runnable passed to its constructor.
 */
public class ThreadCreationPuzzlesDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("-- Scene 1: Does main() start a new thread? --");
        startingNewThread();

        System.out.println("\n-- Scene 2: Override run() in anonymous Thread --");
        overrideRunMethodThread();
        System.out.println();
    }

    // ── Scene 1 ───────────────────────────────────────────────────────────────

    /**
     * Does the main() method start a new thread?
     * Yes.
     *  + No.
     * */
    static void startingNewThread() throws InterruptedException {
        // This runs on the main thread
        System.out.println("Main thread running");

        // Create a new thread
        Thread thread = new Thread(() -> System.out.println("New thread running"));

        // Start the new thread
        thread.start(); // This starts a new thread

        thread.join();  // keeps the output of the scenes from interleaving
    }

    /*
     * The main() method in Java does not start a new thread by itself.
     * Instead, it runs on the main thread of the Java Virtual Machine (JVM) when the program is executed.
     *
     * If you want to start a new thread, you would need to create an instance of the Thread class or
     * implement the Runnable interface and then call the start() method on that thread instance (as it is in this example).
     * */

    // ── Scene 2 ───────────────────────────────────────────────────────────────

    static void overrideRunMethodThread() throws InterruptedException {

        Runnable r = () -> System.out.print("Cat");

        Thread t = new Thread(r) {
            public void run() {
                System.out.print("Dog");
            }
        };
        t.start();

        t.join();   // keeps the output of the scenes from interleaving

        //output: Dog
    }

    /*
     * Output:
     * Since the overridden run() method that prints "Dog" is the effective run() method of the Thread,
     * when the thread starts, it executes this method, resulting in the output being "Dog".
     *
     * Conclusion:
     * The Runnable instance r is not actually executed because the Thread is using its own run() method
     * defined in the anonymous inner class. Hence, the output of the program is "Dog".
     * */
}
