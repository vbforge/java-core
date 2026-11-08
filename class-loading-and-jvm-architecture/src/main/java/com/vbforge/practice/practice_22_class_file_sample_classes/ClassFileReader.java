package com.vbforge.practice.practice_22_class_file_sample_classes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared fixture (not a concept): a minimal class-file parser. It reads the header, the
 * constant pool (keeping the Utf8 entries), and for every method the Code attribute
 * (max_stack, max_locals, bytecode). A small opcode table turns bytecode into mnemonics.
 */
public final class ClassFileReader {

    private static final Map<Integer, String> SIMPLE_OPCODES = new HashMap<>();

    static {
        SIMPLE_OPCODES.put(0x1a, "iload_0");
        SIMPLE_OPCODES.put(0x1b, "iload_1");
        SIMPLE_OPCODES.put(0x1c, "iload_2");
        SIMPLE_OPCODES.put(0x1d, "iload_3");
        SIMPLE_OPCODES.put(0x1e, "lload_0");
        SIMPLE_OPCODES.put(0x27, "dload_1");
        SIMPLE_OPCODES.put(0x2a, "aload_0");
        SIMPLE_OPCODES.put(0x60, "iadd");
        SIMPLE_OPCODES.put(0x61, "ladd");
        SIMPLE_OPCODES.put(0x63, "dadd");
        SIMPLE_OPCODES.put(0x68, "imul");
        SIMPLE_OPCODES.put(0x87, "i2d");
        SIMPLE_OPCODES.put(0xac, "ireturn");
        SIMPLE_OPCODES.put(0xad, "lreturn");
        SIMPLE_OPCODES.put(0xaf, "dreturn");
        SIMPLE_OPCODES.put(0xb0, "areturn");
        SIMPLE_OPCODES.put(0xb1, "return");
    }

    private ClassFileReader() {
    }

    public static final class ClassFile {
        public final int magic;
        public final int minor;
        public final int major;
        public final int constantPoolCount;
        public final List<String> utf8Constants;
        public final List<MethodInfo> methods;

        ClassFile(int magic, int minor, int major, int constantPoolCount,
                  List<String> utf8Constants, List<MethodInfo> methods) {
            this.magic = magic;
            this.minor = minor;
            this.major = major;
            this.constantPoolCount = constantPoolCount;
            this.utf8Constants = utf8Constants;
            this.methods = methods;
        }

        public MethodInfo method(String name) {
            for (MethodInfo method : methods) {
                if (method.name.equals(name)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("no method " + name);
        }
    }

    public static final class MethodInfo {
        public final int accessFlags;
        public final String name;
        public final String descriptor;
        public final int maxStack;
        public final int maxLocals;
        public final byte[] code;

        MethodInfo(int accessFlags, String name, String descriptor, int maxStack, int maxLocals, byte[] code) {
            this.accessFlags = accessFlags;
            this.name = name;
            this.descriptor = descriptor;
            this.maxStack = maxStack;
            this.maxLocals = maxLocals;
            this.code = code;
        }

        public boolean isStatic() {
            return (accessFlags & 0x0008) != 0;
        }

        public List<String> mnemonics() {
            return ClassFileReader.mnemonics(code);
        }
    }

    public static ClassFile parse(byte[] bytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        int magic = in.readInt();
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();

        int poolCount = in.readUnsignedShort();
        String[] utf8 = new String[poolCount];
        for (int i = 1; i < poolCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1:
                    utf8[i] = in.readUTF();
                    break;
                case 3:
                case 4:
                    in.readInt();
                    break;
                case 5:
                case 6:
                    in.readLong();
                    i++;            // long and double occupy two constant-pool slots
                    break;
                case 7:
                case 8:
                case 16:
                case 19:
                case 20:
                    in.readUnsignedShort();
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                case 17:
                case 18:
                    in.readInt();
                    break;
                case 15:
                    in.readUnsignedByte();
                    in.readUnsignedShort();
                    break;
                default:
                    throw new IOException("unknown constant-pool tag " + tag + " at index " + i);
            }
        }

        in.readUnsignedShort();                 // access_flags
        in.readUnsignedShort();                 // this_class
        in.readUnsignedShort();                 // super_class
        int interfaces = in.readUnsignedShort();
        for (int i = 0; i < interfaces; i++) {
            in.readUnsignedShort();
        }

        int fields = in.readUnsignedShort();
        for (int i = 0; i < fields; i++) {
            in.readUnsignedShort();             // access_flags
            in.readUnsignedShort();             // name_index
            in.readUnsignedShort();             // descriptor_index
            skipAttributes(in);
        }

        List<MethodInfo> methods = new ArrayList<>();
        int methodCount = in.readUnsignedShort();
        for (int m = 0; m < methodCount; m++) {
            int accessFlags = in.readUnsignedShort();
            String name = utf8[in.readUnsignedShort()];
            String descriptor = utf8[in.readUnsignedShort()];
            int maxStack = -1;
            int maxLocals = -1;
            byte[] code = new byte[0];

            int attributes = in.readUnsignedShort();
            for (int a = 0; a < attributes; a++) {
                String attributeName = utf8[in.readUnsignedShort()];
                byte[] payload = new byte[in.readInt()];
                in.readFully(payload);
                if ("Code".equals(attributeName)) {
                    DataInputStream codeIn = new DataInputStream(new ByteArrayInputStream(payload));
                    maxStack = codeIn.readUnsignedShort();
                    maxLocals = codeIn.readUnsignedShort();
                    code = new byte[codeIn.readInt()];
                    codeIn.readFully(code);
                }
            }
            methods.add(new MethodInfo(accessFlags, name, descriptor, maxStack, maxLocals, code));
        }

        List<String> utf8Constants = new ArrayList<>();
        for (String value : utf8) {
            if (value != null) {
                utf8Constants.add(value);
            }
        }
        return new ClassFile(magic, minor, major, poolCount, utf8Constants, methods);
    }

    private static void skipAttributes(DataInputStream in) throws IOException {
        int attributes = in.readUnsignedShort();
        for (int a = 0; a < attributes; a++) {
            in.readUnsignedShort();             // attribute_name_index
            byte[] payload = new byte[in.readInt()];
            in.readFully(payload);
        }
    }

    /** Decodes only the opcodes the sample classes use; anything else is shown as op_0xNN. */
    public static List<String> mnemonics(byte[] code) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < code.length; i++) {
            int opcode = code[i] & 0xFF;
            if (opcode == 0x12) {
                result.add("ldc");
                i += 1;                         // one operand byte: constant-pool index
            } else if (opcode == 0xb7) {
                result.add("invokespecial");
                i += 2;                         // two operand bytes: constant-pool index
            } else if (SIMPLE_OPCODES.containsKey(opcode)) {
                result.add(SIMPLE_OPCODES.get(opcode));
            } else {
                result.add(String.format("op_0x%02x", opcode));
            }
        }
        return result;
    }
}
