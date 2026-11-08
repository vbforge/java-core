package com.vbforge.practice.practice_9_load_without_initializing;

/** Fixture: a class whose static initializer announces itself. */
public class LoudPlugin {

    static {
        System.out.println("   [LoudPlugin.<clinit>] static initializer runs");
    }

    public static void touch() {
        System.out.println("   LoudPlugin.touch() called");
    }
}
