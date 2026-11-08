package com.vbforge.practice.practice_8_serial_version_uid;

import java.io.ObjectStreamClass;
import java.io.Serializable;

/**
 * Without a declaration the JVM COMPUTES the version from the class structure (name, fields, methods ...).
 * It is stable for one compiled class, but changes when the class changes - so declare it yourself.
 */
public class ComputedUidDemo {

    static class NoDeclaration implements Serializable {
        int value;
    }

    public static void main(String[] args) {
        long first = ObjectStreamClass.lookup(NoDeclaration.class).getSerialVersionUID();
        long second = ObjectStreamClass.lookup(NoDeclaration.class).getSerialVersionUID();

        System.out.println("computed (non-zero): " + (first != 0));
        System.out.println("stable between lookups: " + (first == second));
    }
}
