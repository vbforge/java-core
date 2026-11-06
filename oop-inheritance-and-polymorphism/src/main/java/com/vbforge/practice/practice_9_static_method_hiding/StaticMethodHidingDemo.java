package com.vbforge.practice.practice_9_static_method_hiding;

/**
 * Early vs late binding: STATIC vs INSTANCE methods.
 *
 *   - STATIC method   -> hidden, resolved by the REFERENCE (declared) type at COMPILE time
 *   - INSTANCE method -> overridden, resolved by the OBJECT type at RUNTIME
 */
public class StaticMethodHidingDemo {

    static class Insurance {
        public static final int LOW = 100;
        public int getPremium()           { return LOW; }
        public static String getCategory(){ return "Insurance"; }
    }

    static class CarInsurance extends Insurance {
        public static final int HIGH = 200;
        @Override public int getPremium()  { return HIGH; }
        public static String getCategory() { return "CarInsurance"; }   // hides, does NOT override
    }

    public static void main(String[] args) {
        Insurance current = new CarInsurance();

        // EARLY binding - static methods are hidden, the declared type decides
        System.out.println("category via Insurance reference: " + current.getCategory());     // Insurance
        System.out.println("category via CarInsurance class:   " + CarInsurance.getCategory()); // CarInsurance

        // LATE binding - instance methods are overridden, the object type decides
        System.out.println("premium: " + current.getPremium());                               // 200
    }
}
