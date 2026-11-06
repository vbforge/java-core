package com.vbforge.practice.practice_21_overload_resolution_phases;

/**
 * Overload resolution, phase 2: unboxing followed by widening.
 *
 * Overloads: doJobInt(int) and doJobInt(Double).
 *   - byte argument: phase 1 finds doJobInt(int) by widening byte -> int
 *   - Byte argument: phase 1 finds nothing; phase 2 unboxes Byte -> byte, then widens byte -> int
 */
public class UnboxThenWidenDemo {

    static void doJobInt(int i)    { System.out.println("int");    }
    static void doJobInt(Double d) { System.out.println("Double"); }

    public static void main(String[] args) {
        byte b  = 5;
        Byte bb = b;
        doJobInt(b);    // int (phase 1: widening byte -> int)
        doJobInt(bb);   // int (phase 2: unbox Byte -> byte, widen byte -> int)
    }
}
