package com.vbforge.practice.practice_3_object_streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** An object stream can mix objects and primitives; they must be read back in EXACTLY the order they were written. */
public class ReadInWriteOrderDemo {

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject("first");
            out.writeInt(42);
            out.writeUTF("utf text");
            out.writeObject(new ArrayList<>(Arrays.asList(1, 2, 3)));
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            String s = (String) in.readObject();
            int i = in.readInt();
            String utf = in.readUTF();
            @SuppressWarnings("unchecked")
            List<Integer> list = (List<Integer>) in.readObject();
            System.out.println(s + " | " + i + " | " + utf + " | " + list);
        }
    }
}
