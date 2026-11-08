package com.vbforge.practice.practice_7_bytecode_size_of_methods;

import com.vbforge.practice.practice_7_bytecode_size_of_methods.ClassFileMethodSizes;

import java.io.IOException;
import java.util.Map;

/**
 * Inlining limits are bytecode BYTES, not source lines.
 * Three methods compute the same sum; only their bytecode size differs.
 * The class reads its own .class file and classifies each method like HotSpot would.
 */
public class BytecodeSizeDemo {

    static int tiny(int a, int b) {
        return a + b;
    }

    static int medium(int a, int b) {
        int r = a + b;
        for (int i = 0; i < 3; i++) {
            r = r * 31 + i;
        }
        r = r % 1_000_003;
        if (r < 0) {
            r = -r;
        }
        return r;
    }

    /** Same idea as tiny(), padded with no-op lines the JIT would delete: the BYTECODE is still big. */
    static int bloated(int a, int b) {
        int r = a + b;
        r = (r ^ 0) + 0;   // no-op line 1
        r = (r ^ 0) + 0;   // no-op line 2
        r = (r ^ 0) + 0;   // no-op line 3
        r = (r ^ 0) + 0;   // no-op line 4
        r = (r ^ 0) + 0;   // no-op line 5
        r = (r ^ 0) + 0;   // no-op line 6
        r = (r ^ 0) + 0;   // no-op line 7
        r = (r ^ 0) + 0;   // no-op line 8
        r = (r ^ 0) + 0;   // no-op line 9
        r = (r ^ 0) + 0;   // no-op line 10
        r = (r ^ 0) + 0;   // no-op line 11
        r = (r ^ 0) + 0;   // no-op line 12
        r = (r ^ 0) + 0;   // no-op line 13
        r = (r ^ 0) + 0;   // no-op line 14
        r = (r ^ 0) + 0;   // no-op line 15
        r = (r ^ 0) + 0;   // no-op line 16
        r = (r ^ 0) + 0;   // no-op line 17
        r = (r ^ 0) + 0;   // no-op line 18
        r = (r ^ 0) + 0;   // no-op line 19
        r = (r ^ 0) + 0;   // no-op line 20
        r = (r ^ 0) + 0;   // no-op line 21
        r = (r ^ 0) + 0;   // no-op line 22
        r = (r ^ 0) + 0;   // no-op line 23
        r = (r ^ 0) + 0;   // no-op line 24
        r = (r ^ 0) + 0;   // no-op line 25
        r = (r ^ 0) + 0;   // no-op line 26
        r = (r ^ 0) + 0;   // no-op line 27
        r = (r ^ 0) + 0;   // no-op line 28
        r = (r ^ 0) + 0;   // no-op line 29
        r = (r ^ 0) + 0;   // no-op line 30
        r = (r ^ 0) + 0;   // no-op line 31
        r = (r ^ 0) + 0;   // no-op line 32
        r = (r ^ 0) + 0;   // no-op line 33
        r = (r ^ 0) + 0;   // no-op line 34
        r = (r ^ 0) + 0;   // no-op line 35
        r = (r ^ 0) + 0;   // no-op line 36
        r = (r ^ 0) + 0;   // no-op line 37
        r = (r ^ 0) + 0;   // no-op line 38
        r = (r ^ 0) + 0;   // no-op line 39
        r = (r ^ 0) + 0;   // no-op line 40
        r = (r ^ 0) + 0;   // no-op line 41
        r = (r ^ 0) + 0;   // no-op line 42
        r = (r ^ 0) + 0;   // no-op line 43
        r = (r ^ 0) + 0;   // no-op line 44
        r = (r ^ 0) + 0;   // no-op line 45
        r = (r ^ 0) + 0;   // no-op line 46
        r = (r ^ 0) + 0;   // no-op line 47
        r = (r ^ 0) + 0;   // no-op line 48
        r = (r ^ 0) + 0;   // no-op line 49
        r = (r ^ 0) + 0;   // no-op line 50
        r = (r ^ 0) + 0;   // no-op line 51
        r = (r ^ 0) + 0;   // no-op line 52
        r = (r ^ 0) + 0;   // no-op line 53
        r = (r ^ 0) + 0;   // no-op line 54
        r = (r ^ 0) + 0;   // no-op line 55
        r = (r ^ 0) + 0;   // no-op line 56
        r = (r ^ 0) + 0;   // no-op line 57
        r = (r ^ 0) + 0;   // no-op line 58
        r = (r ^ 0) + 0;   // no-op line 59
        r = (r ^ 0) + 0;   // no-op line 60
        return r;
    }

    static String classify(int bytes) {
        if (bytes <= 35) {
            return "<= MaxInlineSize(35): inlined whenever the call site runs";
        }
        if (bytes <= 325) {
            return "<= FreqInlineSize(325): inlined only at hot call sites";
        }
        if (bytes < 8000) {
            return "> 325: never inlined, but still compiled";
        }
        return ">= HugeMethodLimit(8000): never compiled";
    }

    public static void main(String[] args) throws IOException {
        Map<String, Integer> sizes = ClassFileMethodSizes.of(BytecodeSizeDemo.class);
        for (String name : new String[]{"tiny(II)I", "medium(II)I", "bloated(II)I"}) {
            int bytes = sizes.get(name);
            System.out.println(String.format("%-14s %4d bytes  %s", name, bytes, classify(bytes)));
        }
        System.out.println("results: tiny=" + tiny(2, 3) + ", medium=" + medium(2, 3) + ", bloated=" + bloated(2, 3));
    }
}
