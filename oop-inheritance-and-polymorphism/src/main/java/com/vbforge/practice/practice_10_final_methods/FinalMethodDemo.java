package com.vbforge.practice.practice_10_final_methods;

/**
 * final method vs overridable method.
 *
 *   Shape.getColor() is final  -> Circle cannot override it
 *   Shape.describe() is not    -> Circle overrides it
 */
public class FinalMethodDemo {

    public static void main(String[] args) {
        Circle circle = new Circle("Red", 5.0);
        System.out.println(circle.describe());                    // overridden in Circle
        System.out.println("color via final getColor(): " + circle.getColor());  // final, inherited as is
    }
}
