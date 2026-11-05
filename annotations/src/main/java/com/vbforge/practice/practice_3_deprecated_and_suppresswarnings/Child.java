package com.vbforge.practice.practice_3_deprecated_and_suppresswarnings;

public class Child extends Parent {
    @Override
    //@Deprecated(since = "1.2", forRemoval = true)  //-->language 9
    public void display() {
        System.out.println("Child.class--display()");
    }
}
