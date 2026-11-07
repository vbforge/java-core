package com.vbforge.practice.practice_3_thread_from_anonymous_runnable;

/** Starting a Thread with an anonymous Runnable. */
public class ThreadFromAnonymousRunnableDemo {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello from anonymous class!");
            }
        });

        thread.start();
        thread.join();   // wait, so the line above is always printed before main ends
    }
}
