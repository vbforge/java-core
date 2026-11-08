package com.vbforge.practice.practice_3_object_streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** readObject has no hasNext(): reading until the end of the stream is signalled by EOFException. */
public class EndOfStreamDemo {

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject("one");
            out.writeObject("two");
            out.writeObject("three");
        }

        int count = 0;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            while (true) {
                try {
                    System.out.println("read: " + in.readObject());
                    count++;
                } catch (EOFException end) {
                    System.out.println("EOFException: no more objects");
                    break;
                }
            }
        }
        System.out.println("objects read: " + count);
    }
}
