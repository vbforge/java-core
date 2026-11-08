package com.vbforge.practice.practice_16_call_stack_frames;

import java.util.ArrayList;
import java.util.List;

/**
 * Every method call pushes one frame on the calling thread's stack.
 * new Throwable().getStackTrace() is a snapshot of those frames, innermost first.
 */
public class CallStackSnapshotDemo {

    public static void main(String[] args) {
        a();
    }

    static void a() {
        b();
    }

    static void b() {
        c();
    }

    static void c() {
        List<String> names = new ArrayList<>();
        for (StackTraceElement frame : new Throwable().getStackTrace()) {
            names.add(frame.getMethodName());
        }
        System.out.println("frames, innermost first: " + String.join(" <- ", names));
    }
}
