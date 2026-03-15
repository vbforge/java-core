package com.vbforge.basics;

import java.util.ArrayList;

/**
 * ═══════════════════════════════════════════════════════════════
 *  BASICS INTERVIEW REFERENCE
 *  Topics: primitive types · casting · wrapper cache · var ·
 *          operators · control flow · String pool
 * ═══════════════════════════════════════════════════════════════
 *
 * HOW TO USE: Read top-to-bottom. Every method is self-contained.
 * Run main() to see output. Add your own snippets freely.
 */
@SuppressWarnings({"all"})
public class BasicsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. PRIMITIVE TYPES — sizes and ranges
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Type     │ Size   │ Min                  │ Max
     *  ─────────┼────────┼──────────────────────┼──────────────────────
     *  byte     │ 8-bit  │ -128                 │ 127
     *  short    │ 16-bit │ -32,768              │ 32,767
     *  int      │ 32-bit │ -2,147,483,648       │ 2,147,483,647
     *  long     │ 64-bit │ -9.2E18              │ 9.2E18          (L suffix)
     *  float    │ 32-bit │ ~1.4E-45             │ ~3.4E38         (f suffix)
     *  double   │ 64-bit │ ~4.9E-324            │ ~1.8E308
     *  char     │ 16-bit │ '\u0000' (0)         │ '\uffff' (65535) unsigned!
     *  boolean  │ ~1-bit │ false                │ true            (JVM-dependent size)
     *
     *  Default values for fields (NOT local variables):
     *  byte/short/int/long → 0   float/double → 0.0   char → '\u0000'
     *  boolean → false           Object refs  → null
     */
    static void primitiveTypes() {
        // Numeric literal formats
        int decimal = 255;
        int hex     = 0xFF;          // same value
        int binary  = 0b11111111;    // same value — Java 7+
        int octal   = 0377;          // same value

        int million = 1_000_000;     // underscores for readability — Java 7+
        long big    = 9_223_372_036_854_775_807L; // L is required for long literals

        // char is unsigned — arithmetic promotes to int
        char c    = 'A';
        int  code = c;               // widening: 65
        char next = (char)(c + 1);   // explicit cast back: 'B'
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. TYPE CASTING GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void castingGotchas() {
        // ✅ Widening — always safe, implicit
        int    i = 42;
        long   l = i;      // int → long
        double d = l;      // long → double

        // ⚠️ Narrowing — requires explicit cast, may LOSE DATA
        double pi        = 3.99;
        int    truncated = (int) pi;   // → 3  NOT 4 — truncates toward zero, never rounds!

        // ⚠️ Narrowing with negatives — also toward zero
        int neg = (int)(-3.99);        // → -3  NOT -4

        // ⚠️ Overflow on narrowing — bits are silently discarded
        long   bigLong  = 1_000_000_000_000L;
        int    overflow = (int) bigLong;   // garbage — no exception thrown!

        // ⚠️ Compound assignment hides implicit narrowing cast
        byte b = 10;
        b += 5;            // compiles — implicit (byte)(b+5)
        // b = b + 5;      // COMPILE ERROR — b+5 is int, can't assign to byte without cast
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. INTEGER CACHE — the classic == trap ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  JVM caches Integer instances for values -128 to 127 (inclusive).
     *  Outside this range, new instances are created each time.
     *  Same cache exists for: Byte, Short, Long (-128..127), Character (0..127), Boolean.
     *
     *  RULE: ALWAYS use .equals() for wrapper type comparison — never ==
     */
    static void integerCacheGotcha() {
        Integer a = 127;   Integer b = 127;
        Integer x = 128;   Integer y = 128;

        System.out.println(a == b);        // true  — same cached instance
        System.out.println(x == y);        // false — different instances!
        System.out.println(x.equals(y));   // true  — always correct

        // ⚠️ Unboxing NPE trap
        Integer nullInt = null;
        // int val = nullInt;  // NullPointerException at runtime — unboxing null!

        // ⚠️ Performance: avoid autoboxing in tight loops
        Long sum = 0L;
        for (long k = 0; k < 1000; k++) {
            sum += k;   // creates ~1000 Long objects via autoboxing!
        }
        // ✅ Use primitive long instead:
        long primitiveSum = 0L;
        for (long k = 0; k < 1000; k++) {
            primitiveSum += k;   // no boxing
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. STRING POOL & EQUALITY ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  String literals → stored in the String Pool (heap metaspace).
     *  new String(...) → always creates a new heap object, bypasses the pool.
     *  String.intern() → returns the pooled instance (or adds to pool).
     *
     *  String is IMMUTABLE — every modification creates a new object.
     *  StringBuilder is MUTABLE — use for string building in loops.
     *  StringBuffer is MUTABLE + thread-safe (synchronized) — rarely needed today.
     */
    static void stringGotchas() {
        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");
        String s4 = s3.intern();

        System.out.println(s1 == s2);        // true  — same pool reference
        System.out.println(s1 == s3);        // false — s3 bypasses pool
        System.out.println(s1 == s4);        // true  — intern() returns pool reference
        System.out.println(s1.equals(s3));   // true  — always use equals() for content!

        // ⚠️ + in loop creates O(n²) garbage
        String result = "";
        for (int i = 0; i < 100; i++) {
            result += i;    // creates new String object each iteration
        }
        // ✅ Use StringBuilder: O(n)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(i);
        }
        String efficient = sb.toString();

        // String comparison edge cases
        String upper = "HELLO";
        System.out.println("hello".equalsIgnoreCase(upper));    // true
        System.out.println("hello".compareTo("world"));         // negative (h < w)
        System.out.println("abc".startsWith("ab"));             // true
        System.out.println("".isEmpty());                       // true
        System.out.println("  ".isBlank());                     // true — Java 11+
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. OPERATORS GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void operatorGotchas() {
        // ⚠️ Integer division truncates — does NOT round
        System.out.println(5 / 2);           // 2,  not 2.5
        System.out.println((double)5 / 2);   // 2.5

        // ⚠️ % (remainder) sign follows the DIVIDEND, not divisor
        System.out.println(-7 % 3);    // -1  (not 2!)
        System.out.println(7 % -3);    //  1  (not -2!)

        // ⚠️ Pre vs post increment — classic interview trick
        int x = 5;
        int a = x++;   // a=5, x=6  (returns THEN increments)
        int b = ++x;   // b=7, x=7  (increments THEN returns)

        // ⚠️ Operator precedence surprises
        int res1 = 2 + 3 * 4;          // 14, not 20 (* before +)
        boolean res2 = true || false && false; // true (&&  before ||)

        // ⚠️ Short-circuit vs non-short-circuit
        int[] counter = {0};
        boolean r1 = false && (++counter[0] > 0);  // counter stays 0 — right not evaluated
        boolean r2 = false &  (++counter[0] > 0);  // counter becomes 1 — both evaluated

        // Bitwise tricks worth knowing
        boolean isEven = (6 & 1) == 0;   // true  — faster than % 2
        int doubled    = 3 << 1;          // 6     — left shift = multiply by 2^n
        int halved     = 8 >> 1;          // 4     — right shift = divide by 2^n
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 6. var — LOCAL TYPE INFERENCE (Java 10+) ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  var infers the type at COMPILE TIME — it is NOT dynamic typing.
     *  Once inferred, the type is fixed and cannot change.
     *
     *  ✅ Allowed:  local variables with initializer
     *  ❌ Forbidden: fields, method parameters, return types, null initializer
     */
    static void varGotchas() {
        var list    = new ArrayList<String>();  // inferred: ArrayList<String>
        var message = "hello";                  // inferred: String
        var number  = 42;                       // inferred: int (NOT Integer)

        // ⚠️ Type is locked — won't compile:
        // number = "text";

        // ⚠️ var with array literals — need explicit type on right side
        // var arr = {1, 2, 3};  // COMPILE ERROR
        var arr = new int[]{1, 2, 3};  // ✅ works

        // ⚠️ var infers the DECLARED type, not runtime type
        var obj = "hello";    // inferred as String, not Object
        // obj = 42;          // COMPILE ERROR — type is String

        // ✅ var shines with long generic types
        var map = new java.util.HashMap<String, java.util.List<Integer>>();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Comparing wrapper types with ==        → use .equals()
     *  ❌ String concatenation in loops          → use StringBuilder
     *  ❌ Ignoring narrowing cast data loss       → always validate range before cast
     *  ❌ Autoboxing in performance-critical code → use primitives
     *  ❌ Unboxing a potentially-null wrapper     → NullPointerException at runtime
     *  ❌ Using == to compare strings             → use .equals() or .equalsIgnoreCase()
     */

    public static void main(String[] args) {
        System.out.println("=== Casting ===");      castingGotchas();
        System.out.println("=== Integer Cache ==="); integerCacheGotcha();
        System.out.println("=== Strings ===");       stringGotchas();
        System.out.println("=== Operators ===");     operatorGotchas();
    }
}
