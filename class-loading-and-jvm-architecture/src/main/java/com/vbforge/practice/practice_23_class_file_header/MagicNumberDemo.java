package com.vbforge.practice.practice_23_class_file_header;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Every class file starts with 0xCAFEBABE. Java bytes are signed, so bytes[0] is -54, not 202:
 * compare with a mask (b & 0xFF) or read the four bytes as one int.
 */
public class MagicNumberDemo {

    public static void main(String[] args) throws IOException {
        byte[] bytes = ClassBytes.read(SampleMath.class);

        System.out.println("first four bytes as Java sees them: "
                + bytes[0] + " " + bytes[1] + " " + bytes[2] + " " + bytes[3]);
        System.out.println("bytes[0] == 0xCA            : " + (bytes[0] == 0xCA) + "   (-54 is not 202)");
        System.out.println("(bytes[0] & 0xFF) == 0xCA   : " + ((bytes[0] & 0xFF) == 0xCA));

        int magic = new DataInputStream(new ByteArrayInputStream(bytes)).readInt();
        System.out.println("readInt() == 0xCAFEBABE     : " + (magic == 0xCAFEBABE));
        System.out.println("as hex                      : " + Integer.toHexString(magic).toUpperCase());
    }
}
