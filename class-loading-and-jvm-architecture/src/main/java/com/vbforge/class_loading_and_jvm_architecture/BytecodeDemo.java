package com.vbforge.class_loading_and_jvm_architecture;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reading bytecode: the .class file layout and what {@code javap} shows.
 *
 * <p>Sections:
 * <ol>
 *   <li>The class-file header and constant pool</li>
 *   <li>Methods and their {@code Code} attribute</li>
 *   <li>Local-variable slots: why instance methods start at {@code iload_1}</li>
 *   <li>The default constructor</li>
 * </ol>
 *
 * <p>Try it yourself after {@code mvn compile}:
 * <pre>
 * javap -c -p -cp target/classes 'com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo$SampleMath'
 * javap -v    -cp target/classes 'com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo$SampleMath'
 * </pre>
 * The tests in this module parse the same bytes with the small parser below and assert what javap prints.
 */
public class BytecodeDemo {

    /** The class we dissect. Kept tiny so its bytecode fits in your head. */
    public static class SampleMath {
        public static final String LITERAL = "bytecode-demo-literal";

        public int add(int a, int b) {
            return a + b;
        }

        public static int square(int x) {
            return x * x;
        }
    }

    // =========================================================================
    // 1. Header and constant pool
    // =========================================================================

    /*
     * WHAT: A .class file starts with the magic number 0xCAFEBABE, a minor and a major version,
     *       then the constant pool: a table of names, descriptors and literals that the rest of
     *       the file refers to by index.
     *
     * WHY:  The major version tells the JVM whether it can run the file
     *       (52 = Java 8, 55 = Java 11, 61 = Java 17, 65 = Java 21; for Java 5+ it is release + 44).
     *       The constant pool is why method names, descriptors like "(II)I" and string constants
     *       are all visible in the file as UTF-8 entries.
     *
     * COMPLEXITY: entries are addressed by index, so a lookup is O(1).
     *
     * GOTCHA: constant_pool_count is ONE MORE than the number of usable entries (index 0 is unused),
     *         and long/double constants take two slots.
     */

    /** One method of a class file. {@code code} is {@code null} for abstract and native methods. */
    public static final class MethodInfo {
        public final String name;
        public final String descriptor;
        public final byte[] code;
        public final int maxStack;
        public final int maxLocals;

        MethodInfo(String name, String descriptor, byte[] code, int maxStack, int maxLocals) {
            this.name = name;
            this.descriptor = descriptor;
            this.code = code;
            this.maxStack = maxStack;
            this.maxLocals = maxLocals;
        }
    }

    /** The parts of a class file this demo cares about. */
    public static final class ClassFileInfo {
        public final long magic;
        public final int minorVersion;
        public final int majorVersion;
        public final int constantPoolCount;
        public final List<String> utf8Constants;
        public final List<MethodInfo> methods;

        ClassFileInfo(long magic, int minorVersion, int majorVersion, int constantPoolCount,
                      List<String> utf8Constants, List<MethodInfo> methods) {
            this.magic = magic;
            this.minorVersion = minorVersion;
            this.majorVersion = majorVersion;
            this.constantPoolCount = constantPoolCount;
            this.utf8Constants = Collections.unmodifiableList(utf8Constants);
            this.methods = Collections.unmodifiableList(methods);
        }

        /** The method with this name and descriptor, or {@code null}. */
        public MethodInfo method(String name, String descriptor) {
            for (MethodInfo m : methods) {
                if (m.name.equals(name) && m.descriptor.equals(descriptor)) {
                    return m;
                }
            }
            return null;
        }
    }

    /** Parses the header, the UTF-8 constants and the methods of a class file. */
    public static ClassFileInfo parse(byte[] classBytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(classBytes));
        long magic = in.readInt() & 0xFFFFFFFFL;
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();

        int poolCount = in.readUnsignedShort();
        String[] utf8 = new String[poolCount];
        List<String> utf8List = new ArrayList<>();
        for (int i = 1; i < poolCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1: // Utf8
                    utf8[i] = in.readUTF();
                    utf8List.add(utf8[i]);
                    break;
                case 3: // Integer
                case 4: // Float
                case 9: // Fieldref
                case 10: // Methodref
                case 11: // InterfaceMethodref
                case 12: // NameAndType
                case 17: // Dynamic
                case 18: // InvokeDynamic
                    in.skipBytes(4);
                    break;
                case 5: // Long
                case 6: // Double
                    in.skipBytes(8);
                    i++; // takes two slots
                    break;
                case 7: // Class
                case 8: // String
                case 16: // MethodType
                case 19: // Module
                case 20: // Package
                    in.skipBytes(2);
                    break;
                case 15: // MethodHandle
                    in.skipBytes(3);
                    break;
                default:
                    throw new IOException("Unknown constant-pool tag " + tag);
            }
        }

        in.skipBytes(6); // access_flags, this_class, super_class
        int interfaces = in.readUnsignedShort();
        in.skipBytes(2 * interfaces);

        int fields = in.readUnsignedShort();
        for (int i = 0; i < fields; i++) {
            in.skipBytes(6); // access_flags, name_index, descriptor_index
            skipAttributes(in);
        }

        List<MethodInfo> methods = new ArrayList<>();
        int methodCount = in.readUnsignedShort();
        for (int i = 0; i < methodCount; i++) {
            in.readUnsignedShort(); // access_flags
            String name = utf8[in.readUnsignedShort()];
            String descriptor = utf8[in.readUnsignedShort()];
            byte[] code = null;
            int maxStack = 0;
            int maxLocals = 0;
            int attributes = in.readUnsignedShort();
            for (int a = 0; a < attributes; a++) {
                String attributeName = utf8[in.readUnsignedShort()];
                int length = in.readInt();
                if ("Code".equals(attributeName)) {
                    maxStack = in.readUnsignedShort();
                    maxLocals = in.readUnsignedShort();
                    int codeLength = in.readInt();
                    code = new byte[codeLength];
                    in.readFully(code);
                    in.skipBytes(length - 8 - codeLength); // exception table + nested attributes
                } else {
                    in.skipBytes(length);
                }
            }
            methods.add(new MethodInfo(name, descriptor, code, maxStack, maxLocals));
        }
        return new ClassFileInfo(magic, minor, major, poolCount, utf8List, methods);
    }

    private static void skipAttributes(DataInputStream in) throws IOException {
        int attributes = in.readUnsignedShort();
        for (int i = 0; i < attributes; i++) {
            in.skipBytes(2); // attribute_name_index
            in.skipBytes(in.readInt());
        }
    }

    public static void headerDemo() throws IOException {
        ClassFileInfo info = parse(ClassLoaderDemo.readClassFile(SampleMath.class));
        System.out.printf("magic = 0x%X, major = %d, constant_pool_count = %d%n",
                info.magic, info.majorVersion, info.constantPoolCount);
        System.out.println("UTF-8 constants include the literal: " + info.utf8Constants.contains(SampleMath.LITERAL));
    }

    // =========================================================================
    // 2. Methods and the Code attribute
    // =========================================================================

    /*
     * WHAT: Each method has a Code attribute: max_stack (operand-stack depth), max_locals
     *       (local-variable slots) and the instructions themselves.
     *
     * WHY:  The JVM is a stack machine. `a + b` becomes: push a, push b, add, return.
     *       That is exactly what `javap -c` prints.
     *
     * We decode only the handful of opcodes used by SampleMath; unknown opcodes print as hex.
     */

    /** Mnemonics for the opcodes in {@code code} (limited to the ones this demo uses). */
    public static List<String> mnemonics(byte[] code) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < code.length) {
            int opcode = code[i] & 0xFF;
            result.add(mnemonic(opcode));
            i += 1 + (opcode == 0xB7 ? 2 : 0); // invokespecial has a 2-byte operand
        }
        return result;
    }

    private static String mnemonic(int opcode) {
        switch (opcode) {
            case 0x03: return "iconst_0";
            case 0x04: return "iconst_1";
            case 0x05: return "iconst_2";
            case 0x1A: return "iload_0";
            case 0x1B: return "iload_1";
            case 0x1C: return "iload_2";
            case 0x1D: return "iload_3";
            case 0x2A: return "aload_0";
            case 0x60: return "iadd";
            case 0x68: return "imul";
            case 0xAC: return "ireturn";
            case 0xB1: return "return";
            case 0xB7: return "invokespecial";
            default: return String.format("0x%02x", opcode);
        }
    }

    public static void methodsDemo() throws IOException {
        ClassFileInfo info = parse(ClassLoaderDemo.readClassFile(SampleMath.class));
        for (MethodInfo m : info.methods) {
            System.out.println(m.name + m.descriptor + " stack=" + m.maxStack + " locals=" + m.maxLocals
                    + " " + mnemonics(m.code));
        }
    }

    // =========================================================================
    // 3. Local-variable slots
    // =========================================================================

    /*
     * WHAT: Arguments arrive in local-variable slots. In an INSTANCE method slot 0 holds `this`,
     *       so `add(int a, int b)` reads a and b from slots 1 and 2 (iload_1, iload_2).
     *       In a STATIC method there is no `this`, so `square(int x)` reads slot 0 (iload_0).
     *
     * WHY:  max_locals is therefore 3 for add and 1 for square.
     *
     * GOTCHA: long and double take two slots each.
     */

    // =========================================================================
    // 4. The default constructor
    // =========================================================================

    /*
     * WHAT: A class with no declared constructor still gets one. SampleMath's `<init>()V` is
     *       aload_0, invokespecial Object.<init>, return - five bytes.
     *
     * WHY:  Every constructor must call a super constructor first; the compiler writes that call for you.
     */

    public static void main(String[] args) throws IOException {
        headerDemo();
        System.out.println("---");
        methodsDemo();
    }
}
