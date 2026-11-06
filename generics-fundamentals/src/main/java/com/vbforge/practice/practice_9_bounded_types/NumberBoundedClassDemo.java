package com.vbforge.practice.practice_9_bounded_types;

/**
 * Bounded type parameters on a class: {@code T extends Number} restricts which type
 * arguments are allowed and lets the compiler substitute the bound type wherever T is used.
 * <p>
 * In Div.perform() there is no need to check x and y for Number and cast them before calling
 * doubleValue() - the compiler knows T is a Number.
 */
public class NumberBoundedClassDemo {

    public static void main(String[] args) {

        // ================== Scene 1: Calculator<T extends Number> ==================
        Calculator<Integer> calcIntegers = new Calculator<>();
        System.out.println(calcIntegers.square(10));

        Calculator<Double> calcDouble = new Calculator<>();
        System.out.println(calcDouble.square(20.0));

        Calculator<Float> calcFloat = new Calculator<>();
        System.out.println(calcFloat.square(30.0f));

        // ================== Scene 2: Div<T extends Number> ==================
        double aD = 49.70132117265778;
        double bD = 14.655876893531572;
        short aS = 86;
        short bS = 28;
        Div<Double> divD = new Div<>(aD, bD);
        System.out.println(aD + " : " + bD + " = " + divD.perform());   // 3
        Div<Short> divS = new Div<>(aS, bS);
        System.out.println(aS + " : " + bS + " = " + divS.perform());   // 3
        // Div<Character> obj3 = new Div<>('9', '2'); // compile error - Character is not a subclass of Number
    }

    static class Calculator<T extends Number> {

        public double square(T x) {
            return x.doubleValue() * x.doubleValue();
        }
    }

    static class Div<T extends Number> {
        private final T x;
        private final T y;

        public Div(T x, T y) {
            this.x = x;
            this.y = y;
        }

        public int perform() {
            return (int) (x.doubleValue() / y.doubleValue());
        }
    }
}
