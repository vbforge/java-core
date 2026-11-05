package com.vbforge.practice.practice_2_override_annotation;

public class OverrideAnnotationDemo {
    public static void main(String[] args) {
        Child child = new Child();
        child.display(); //Child.class--display()
        // (if the override had a typo and no @Override, the output would be: Parent.class--display())
    }
}
