package com.vbforge.practice.practice_3_enum_constant_specific_bodies;

/**
 * Abstract method in the enum + one body per constant, plus a String field.
 * The field is private, so the demo reads it through getOperation().
 */
public class ConstantSpecificMethodDemo {

    public static void main(String[] args) {
        int x = 10;
        int y = 2;
        for (BinaryOperation op : BinaryOperation.values())
            System.out.println(x + " " + op.getOperation() + " " + y
                    + " = " + op.calculate(x, y));

        /*  10 + 2 = 12
            10 - 2 = 8
            10 / 2 = 5
            10 * 2 = 20     */
    }
}

enum BinaryOperation {

    PLUS("+") {
        public int calculate(int a, int b) {
            return a + b;
        }
    },
    MINUS("-") {
        public int calculate(int a, int b) {
            return a - b;
        }
    },
    DIVISION("/") {
        public int calculate(int a, int b) {
            return a / b;
        }
    },
    MULTI("*") {
        public int calculate(int a, int b) {
            return a * b;
        }
    };

    private final String operation;

    public abstract int calculate(int a, int b);

    BinaryOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
