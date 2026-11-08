package com.vbforge.practice.practice_3_object_streams;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * ObjectOutputStream buffers primitives (writeInt, writeUTF, ...) until flush() or close().
 * Taking toByteArray() before that loses them - close (or flush) the stream FIRST.
 */
public class BufferedPrimitivesDemo {

    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.flush(); // push the stream header

        int headerOnly = bytes.size();
        out.writeInt(1);
        out.writeInt(2);
        int beforeFlush = bytes.size();
        out.flush();
        int afterFlush = bytes.size();
        out.close();

        System.out.println("header only:  " + headerOnly + " bytes");
        System.out.println("after two writeInt, before flush: " + beforeFlush + " bytes (unchanged: "
                + (beforeFlush == headerOnly) + ")");
        System.out.println("after flush: " + afterFlush + " bytes (grew: " + (afterFlush > beforeFlush) + ")");
    }
}
