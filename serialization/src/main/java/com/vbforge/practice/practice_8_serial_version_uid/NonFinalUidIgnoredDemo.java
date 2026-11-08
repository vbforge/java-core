package com.vbforge.practice.practice_8_serial_version_uid;

import java.io.ObjectStreamClass;
import java.io.Serializable;

/** The field must be static AND final: without 'final' it is silently ignored and the JVM computes a value. */
public class NonFinalUidIgnoredDemo {

    static class MissingFinal implements Serializable {
        private static long serialVersionUID = 5L; // BUG: not final
    }

    public static void main(String[] args) {
        long uid = ObjectStreamClass.lookup(MissingFinal.class).getSerialVersionUID();
        System.out.println("declared 5L without final, lookup says 5: " + (uid == 5L));
    }
}
