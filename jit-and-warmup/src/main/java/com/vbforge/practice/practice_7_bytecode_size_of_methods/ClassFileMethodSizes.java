package com.vbforge.practice.practice_7_bytecode_size_of_methods;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads a .class file and returns the bytecode length of every method (the Code attribute's code_length).
 * That is the number HotSpot compares with MaxInlineSize (35) and FreqInlineSize (325).
 * Same numbers as "javap -c", without needing the JDK tools.
 */
public final class ClassFileMethodSizes {

    private ClassFileMethodSizes() {
    }

    /** Method name + descriptor -> bytecode bytes, in class-file order. */
    public static Map<String, Integer> of(Class<?> type) throws IOException {
        String resource = type.getName().replace('.', '/') + ".class";
        try (InputStream in = type.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("class file not found: " + resource);
            }
            return parse(new DataInputStream(in));
        }
    }

    private static Map<String, Integer> parse(DataInputStream in) throws IOException {
        in.readInt();           // magic
        in.readUnsignedShort(); // minor
        in.readUnsignedShort(); // major

        int cpCount = in.readUnsignedShort();
        String[] utf8 = new String[cpCount];
        for (int i = 1; i < cpCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1 -> utf8[i] = in.readUTF();
                case 3, 4, 9, 10, 11, 12, 17, 18 -> in.skipBytes(4);
                case 5, 6 -> {
                    in.skipBytes(8);
                    i++;        // long and double take two slots
                }
                case 7, 8, 16, 19, 20 -> in.skipBytes(2);
                case 15 -> in.skipBytes(3);
                default -> throw new IOException("unknown constant pool tag " + tag);
            }
        }

        in.skipBytes(6);                                   // access flags, this, super
        in.skipBytes(2 * in.readUnsignedShort());          // interfaces
        int fields = in.readUnsignedShort();
        for (int i = 0; i < fields; i++) {
            in.skipBytes(6);
            skipAttributes(in);
        }

        Map<String, Integer> sizes = new LinkedHashMap<>();
        int methods = in.readUnsignedShort();
        for (int i = 0; i < methods; i++) {
            in.readUnsignedShort();                        // access flags
            String name = utf8[in.readUnsignedShort()];
            String desc = utf8[in.readUnsignedShort()];
            int attrs = in.readUnsignedShort();
            for (int a = 0; a < attrs; a++) {
                String attrName = utf8[in.readUnsignedShort()];
                int length = in.readInt();
                if ("Code".equals(attrName)) {
                    in.skipBytes(4);                       // max stack, max locals
                    sizes.put(name + desc, in.readInt());  // code_length
                    in.skipBytes(length - 8);
                } else {
                    in.skipBytes(length);
                }
            }
        }
        return sizes;
    }

    private static void skipAttributes(DataInputStream in) throws IOException {
        int attrs = in.readUnsignedShort();
        for (int a = 0; a < attrs; a++) {
            in.readUnsignedShort();
            in.skipBytes(in.readInt());
        }
    }
}
