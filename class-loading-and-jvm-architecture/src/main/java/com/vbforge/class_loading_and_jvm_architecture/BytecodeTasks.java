package com.vbforge.class_loading_and_jvm_architecture;

/**
 * Practice tasks for bytecode and class files. Read {@link BytecodeDemo} first.
 */
public class BytecodeTasks {

    /**
     * Check that a byte array starts with the class-file magic number 0xCAFEBABE.
     * Arrays that are {@code null} or shorter than four bytes are not valid.
     *
     * <p>Examples:
     * <pre>
     * hasValidMagic({CA, FE, BA, BE, 0, 0, 0, 52})   → true
     * hasValidMagic({CA, FE, BA})                    → false  (too short)
     * hasValidMagic({00, FE, BA, BE})                → false
     * hasValidMagic(null)                            → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Java bytes are signed: {@code (byte) 0xCA} is negative.</li>
     *   <li>Compare bytes with bytes, or mask with {@code & 0xFF} before comparing as ints.</li>
     * </ul>
     *
     * @param bytes candidate file content, may be null
     * @return true if the first four bytes are CA FE BA BE
     */
    public boolean hasValidMagic(byte[] bytes) {
        throw new UnsupportedOperationException("TODO: implement hasValidMagic");
    }

    /**
     * Convert a class-file major version to the Java release that produced it.
     * Versions 49 and above map to releases 5 and above. Anything below 49 is rejected
     * with {@link IllegalArgumentException} (old releases were named 1.x and are not supported here).
     *
     * <p>Examples:
     * <pre>
     * javaReleaseFor(49)   → 5
     * javaReleaseFor(52)   → 8
     * javaReleaseFor(61)   → 17
     * javaReleaseFor(65)   → 21
     * javaReleaseFor(48)   → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The mapping is a straight line - two data points are enough to find it.</li>
     *   <li>Check the lower bound first.</li>
     * </ul>
     *
     * @param majorVersion the u2 major_version field of a class file
     * @return the Java release number
     */
    public int javaReleaseFor(int majorVersion) {
        throw new UnsupportedOperationException("TODO: implement javaReleaseFor");
    }

    /**
     * Count the local-variable slots needed just for the parameters of a method (plus {@code this}
     * for instance methods), given its JVM descriptor.
     *
     * <p>Slot sizes: {@code J} (long) and {@code D} (double) take 2; every other primitive,
     * object ({@code Lpkg/Type;}) and array ({@code [...}) takes 1. Instance methods need one
     * extra slot for {@code this}. The return type is ignored.
     *
     * <p>Examples:
     * <pre>
     * localSlotsFor("(II)I", false)                         → 3   (this, int, int)
     * localSlotsFor("(II)I", true)                          → 2
     * localSlotsFor("(JD)V", true)                          → 4
     * localSlotsFor("(Ljava/lang/String;[IJ)V", false)      → 5   (this, ref, ref, long = 1+1+1+2)
     * localSlotsFor("()V", true)                            → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Only the text between the parentheses matters.</li>
     *   <li>An object type runs up to the next {@code ;}; an array adds one {@code [} per dimension
     *       in front of an element type but is still a single reference.</li>
     * </ul>
     *
     * @param descriptor method descriptor such as {@code "(II)I"}
     * @param isStatic   true for static methods
     * @return number of slots occupied by {@code this} (if any) and the parameters
     */
    public int localSlotsFor(String descriptor, boolean isStatic) {
        throw new UnsupportedOperationException("TODO: implement localSlotsFor");
    }
}
