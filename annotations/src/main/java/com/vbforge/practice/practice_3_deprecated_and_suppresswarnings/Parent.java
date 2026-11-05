package com.vbforge.practice.practice_3_deprecated_and_suppresswarnings;

public class Parent {
    @SuppressWarnings("unused")
    @Deprecated
    public void display() {
        System.out.println("Parent.class--display()");
    }
}
