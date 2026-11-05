package com.vbforge.practice.practice_4_functional_interface_annotation;

@FunctionalInterface
public interface MyFunctionalInterface {
    void abstractMethod();
    //void anotherAbstractMethod();   // uncomment -> compile error: two abstract methods
}
