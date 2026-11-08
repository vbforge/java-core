package com.vbforge.practice.practice_8_serial_version_uid;

import java.io.ObjectStreamClass;
import java.io.Serializable;

/** A declared serialVersionUID (static final long) is what ObjectStreamClass.lookup reports. */
public class DeclaredUidDemo {

    static class Versioned implements Serializable {
        private static final long serialVersionUID = 42L;
    }

    public static void main(String[] args) {
        ObjectStreamClass desc = ObjectStreamClass.lookup(Versioned.class);
        System.out.println("declared 42L, lookup says: " + desc.getSerialVersionUID());
    }
}
