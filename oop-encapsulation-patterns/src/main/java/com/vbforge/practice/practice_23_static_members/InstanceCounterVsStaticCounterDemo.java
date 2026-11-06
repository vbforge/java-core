package com.vbforge.practice.practice_23_static_members;

/**
 * Counting instances:
 *   - a static field is shared by the whole class: the right place for a counter
 *   - an instance field is created fresh for every object: it is always 1
 */
public class InstanceCounterVsStaticCounterDemo {

    static class PerObjectCounter {
        private int countCars;
        PerObjectCounter() { countCars++; }
        int getCountCars() { return countCars; }
    }

    static class SharedCounter {
        private static int countCars;
        SharedCounter() { countCars++; }
        static int getCountCars() { return countCars; }
    }

    public static void main(String[] args) {
        new PerObjectCounter();
        new PerObjectCounter();
        PerObjectCounter lastPerObject = new PerObjectCounter();
        System.out.println("instance-level counter (useless): " + lastPerObject.getCountCars());   // 1

        new SharedCounter();
        new SharedCounter();
        new SharedCounter();
        System.out.println("static counter (correct):        " + SharedCounter.getCountCars());   // 3
    }
}
