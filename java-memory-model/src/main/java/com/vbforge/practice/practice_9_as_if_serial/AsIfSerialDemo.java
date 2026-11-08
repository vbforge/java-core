package com.vbforge.practice.practice_9_as_if_serial;

/**
 * As-if-serial: the compiler, the JIT and the CPU may reorder independent statements, but
 * the thread that runs them can never tell. Dependent statements are never swapped.
 * Another thread can tell, unless a happens-before edge (here {@code join}) orders things.
 */
public class AsIfSerialDemo {

    private static int x;
    private static int y;

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            x = 1;                 // independent of y: may be executed after the next line
            y = 2;
            int sum = x + y;       // depends on both writes: always sees x == 1 and y == 2
            System.out.println("inside the worker:   x + y = " + sum);
        });
        worker.start();
        worker.join();             // edge: everything the worker did is visible below

        System.out.println("after join in main: x = " + x + ", y = " + y);
    }
}
