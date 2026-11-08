package com.vbforge.practice.practice_9_load_without_initializing;

/** Fixture: a second class whose static initializer announces itself. */
public class EagerPlugin {

    static {
        System.out.println("   [EagerPlugin.<clinit>] static initializer runs");
    }
}
