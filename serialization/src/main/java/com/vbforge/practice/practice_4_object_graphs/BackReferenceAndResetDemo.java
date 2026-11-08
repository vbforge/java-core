package com.vbforge.practice.practice_4_object_graphs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Writing the SAME object twice to one stream stores the second write only as a back-reference:
 * later changes are not written. reset() forgets the written objects so the next write is a full one.
 */
public class BackReferenceAndResetDemo {

    static class Counter implements Serializable {
        private static final long serialVersionUID = 1L;
        int value;
    }

    private static void readTwo(String title, byte[] data) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Counter first = (Counter) in.readObject();
            Counter second = (Counter) in.readObject();
            System.out.println(title + ": first=" + first.value + ", second=" + second.value
                    + ", same instance=" + (first == second));
        }
    }

    public static void main(String[] args) throws Exception {
        Counter c = new Counter();

        ByteArrayOutputStream plain = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(plain)) {
            c.value = 1;
            out.writeObject(c);
            c.value = 2;
            out.writeObject(c); // back-reference: value 2 is NOT written
        }
        readTwo("without reset", plain.toByteArray());

        ByteArrayOutputStream withReset = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(withReset)) {
            c.value = 1;
            out.writeObject(c);
            c.value = 2;
            out.reset();
            out.writeObject(c); // full write again
        }
        readTwo("with reset   ", withReset.toByteArray());
    }
}
