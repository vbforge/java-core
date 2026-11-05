package com.vbforge.practice.practice_4_functional_interface_annotation;

public class FunctionalInterfaceAnnotationDemo implements MyFunctionalInterface {
    public static void main(String[] args) {
        FunctionalInterfaceAnnotationDemo demo = new FunctionalInterfaceAnnotationDemo();
        demo.abstractMethod();

        // a functional interface (one abstract method) can also be implemented by a lambda
        MyFunctionalInterface lambda = () -> System.out.println("abstractMethod() via lambda");
        lambda.abstractMethod();
    }

    @Override
    public void abstractMethod() {
        System.out.println("abstractMethod()");
    }
}
