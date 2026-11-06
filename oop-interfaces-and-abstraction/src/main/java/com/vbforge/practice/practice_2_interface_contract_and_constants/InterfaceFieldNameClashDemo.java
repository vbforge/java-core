package com.vbforge.practice.practice_2_interface_contract_and_constants;

/**
 * Two interfaces declare a constant with the same name. The implementing class compiles,
 * but an unqualified use of LIMIT inside it would be ambiguous, so each one is qualified.
 */
public class InterfaceFieldNameClashDemo {

    interface IntLimits {
        int LIMIT = 100;
        String describe();
    }

    interface DoubleLimits {
        double LIMIT = 200.5;
        String describe();
    }

    static class ClashingImplementer implements IntLimits, DoubleLimits {
        @Override
        public String describe() {
            // return "" + LIMIT;   // compile error: reference to LIMIT is ambiguous
            return "int=" + IntLimits.LIMIT + ", double=" + DoubleLimits.LIMIT;
        }
    }

    public static void main(String[] args) {
        ClashingImplementer obj = new ClashingImplementer();
        System.out.println("describe()           = " + obj.describe());
        System.out.println("IntLimits.LIMIT      = " + IntLimits.LIMIT);
        System.out.println("DoubleLimits.LIMIT   = " + DoubleLimits.LIMIT);
        // obj.LIMIT   // compile error: ambiguous
    }
}
