package com.vbforge.practice.practice_1_anatomy_of_annotation;

/** Runs Base.doAction(), which reads its own @BaseAction (required elements) via reflection. */
public class RequiredElementsDemo {

    public static void main(String[] args) {
        new Base().doAction();
    }
}
