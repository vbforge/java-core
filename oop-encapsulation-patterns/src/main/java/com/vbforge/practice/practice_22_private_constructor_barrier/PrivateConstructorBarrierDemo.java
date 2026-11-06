package com.vbforge.practice.practice_22_private_constructor_barrier;

public class PrivateConstructorBarrierDemo {

    public static void main(String[] args) {
        // new NonInstantiable();   // compile error: NonInstantiable() has private access
        System.out.println("NonInstantiable: " + NonInstantiable.purpose());
    }
}
