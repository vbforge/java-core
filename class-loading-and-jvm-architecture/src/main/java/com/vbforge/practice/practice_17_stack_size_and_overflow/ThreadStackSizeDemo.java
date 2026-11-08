package com.vbforge.practice.practice_17_stack_size_and_overflow;

/**
 * The stack size is a per-thread setting (the stackSize argument of the Thread constructor,
 * -Xss for the default). The recursion depth you reach before StackOverflowError is bounded by it.
 * The exact depths differ between JVMs and runs, so only the comparison is printed.
 */
public class ThreadStackSizeDemo {

    static class DepthProbe implements Runnable {

        int depth;

        void down() {
            depth++;
            down();
        }

        @Override
        public void run() {
            try {
                down();
            } catch (StackOverflowError expected) {
                // this thread's own stack is full; other threads are not affected
            }
        }
    }

    static int depthWithStack(long stackBytes) throws InterruptedException {
        DepthProbe probe = new DepthProbe();
        Thread thread = new Thread(null, probe, "probe-" + stackBytes, stackBytes);
        thread.start();
        thread.join();
        return probe.depth;
    }

    public static void main(String[] args) throws InterruptedException {
        int small = depthWithStack(256L * 1024);
        int big = depthWithStack(8L * 1024 * 1024);

        System.out.println("256 KB stack reached more than 1000 frames: " + (small > 1000));
        System.out.println("a bigger stack allows deeper recursion:     " + (big > small));
    }
}
