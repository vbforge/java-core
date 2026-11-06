package com.vbforge.practice.practice_21_overload_resolution_phases;

/**
 * Overload resolution, phase 3: varargs is the last resort.
 *
 * The compiler tries, in order: (1) exact match / widening, (2) boxing / unboxing,
 * (3) varargs. The first phase that finds a method wins, so a fixed-arity
 * overload always beats a varargs one.
 */
public class VarargsLastResortDemo {

    static void doJobVar(String s)               { System.out.println("String");             }
    static void doJobVar(String s1, String s2)   { System.out.println("String, String");     }
    static void doJobVar(String s1, String... v) { System.out.println("String, String..."); }

    static void printValues(int a)    { System.out.println("Single int: " + a);    }
    static void printValues(double a) { System.out.println("Single double: " + a); }
    static void printValues(int... values) {
        System.out.print("Varargs: ");
        for (int v : values) System.out.print(v + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.print("doJobVar(\"hi\")             -> ");
        doJobVar("hi");                 // String
        System.out.print("doJobVar(\"hi\",\"hi\")       -> ");
        doJobVar("hi", "hi");           // String, String (fixed beats varargs)
        System.out.print("doJobVar(\"hi\",\"hi\",\"hi\")   -> ");
        doJobVar("hi", "hi", "hi");     // String, String... (only varargs matches)

        printValues(10);                // int (exact)
        printValues(10.5f);             // double (float -> double widening)
        printValues(1, 2, 3, 4);        // int... (varargs fallback)
        printValues();                  // int... (zero-length varargs)
    }
}
