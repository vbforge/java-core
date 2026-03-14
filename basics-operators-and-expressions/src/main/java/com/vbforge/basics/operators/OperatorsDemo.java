package com.vbforge.basics.operators;

/**
 * Demonstrates Java operators: arithmetic, assignment, comparison,
 * logical, bitwise, shift, ternary — and operator precedence.
 *
 * <p>Each method isolates one operator category for clarity.
 * The tests in {@code OperatorsDemoTest} verify every example here.
 */
public class OperatorsDemo {

    // ── 1. Arithmetic Operators ────────────────────────────────────────────────
    /**
     * Standard math operators: + - * / %
     *
     * <p>Key gotchas:
     * <ul>
     *   <li>Integer division truncates (5/2 = 2, not 2.5)</li>
     *   <li>% is remainder, not modulo — result sign follows the dividend</li>
     *   <li>Division by zero: int → ArithmeticException, double → Infinity</li>
     * </ul>
     */
    public void arithmeticOperators() {
        int a = 10, b = 3;

        System.out.println("a + b = " + (a + b));   // 13
        System.out.println("a - b = " + (a - b));   // 7
        System.out.println("a * b = " + (a * b));   // 30
        System.out.println("a / b = " + (a / b));   // 3  (integer division — truncates!)
        System.out.println("a % b = " + (a % b));   // 1  (remainder)

        // Integer division vs floating-point division
        double exactDiv = (double) a / b;
        System.out.println("(double)a / b = " + exactDiv); // 3.3333...

        // Remainder with negatives: sign follows the DIVIDEND
        System.out.println("-7 % 3 = "  + (-7 % 3));   // -1 (not 2!)
        System.out.println("7 % -3 = "  + (7 % -3));   //  1 (not -2!)

        // Division by zero
        double inf = 1.0 / 0.0;
        System.out.println("1.0 / 0.0 = " + inf);      // Infinity
        // int zeroDivide = 1 / 0;                      // ← ArithmeticException!
    }


    // ── 2. Increment & Decrement ───────────────────────────────────────────────
    /**
     * Pre (++x): increments FIRST, then returns new value.
     * Post (x++): returns CURRENT value, then increments.
     */
    public void incrementDecrement() {
        int x = 5;

        int postIncResult = x++;        // returns 5, then x becomes 6
        System.out.println("post-increment returned: " + postIncResult + ", x now: " + x);

        int preIncResult = ++x;         // x becomes 7, returns 7
        System.out.println("pre-increment returned: "  + preIncResult  + ", x now: " + x);

        int postDecResult = x--;        // returns 7, then x becomes 6
        System.out.println("post-decrement returned: " + postDecResult + ", x now: " + x);

        int preDecResult = --x;         // x becomes 5, returns 5
        System.out.println("pre-decrement returned: "  + preDecResult  + ", x now: " + x);
    }


    // ── 3. Compound Assignment Operators ──────────────────────────────────────
    /**
     * Shorthand for applying an operation and assigning back.
     * Note: compound assignment includes an IMPLICIT narrowing cast.
     * {@code byte b = 5; b += 3;} compiles fine, but {@code b = b + 3;} does not
     * (b + 3 is promoted to int).
     */
    public void compoundAssignment() {
        int n = 10;

        n += 5;  System.out.println("after +=5:  " + n);  // 15
        n -= 3;  System.out.println("after -=3:  " + n);  // 12
        n *= 2;  System.out.println("after *=2:  " + n);  // 24
        n /= 4;  System.out.println("after /=4:  " + n);  // 6
        n %= 4;  System.out.println("after %=4:  " + n);  // 2

        // Implicit narrowing cast in compound assignment
        byte b = 100;
        b += 27;                        // compiles: implicit (byte)(b + 27)
        System.out.println("byte after +=27: " + b); // 127
    }


    // ── 4. Comparison Operators ────────────────────────────────────────────────
    /**
     * Always return boolean. Work on primitives by value.
     * For objects, == compares REFERENCES — use .equals() for content equality.
     */
    public void comparisonOperators() {
        int a = 5, b = 10;

        System.out.println("a == b: " + (a == b));   // false
        System.out.println("a != b: " + (a != b));   // true
        System.out.println("a <  b: " + (a < b));    // true
        System.out.println("a <= b: " + (a <= b));   // true
        System.out.println("a >  b: " + (a > b));    // false
        System.out.println("a >= b: " + (a >= b));   // false

        // ⚠️ Reference vs value equality for objects
        String s1 = new String("hello");
        String s2 = new String("hello");
        System.out.println("s1 == s2:      " + (s1 == s2));        // false — different objects
        System.out.println("s1.equals(s2): " + s1.equals(s2));     // true  — same content
    }


    // ── 5. Logical Operators ───────────────────────────────────────────────────
    /**
     * && and || are SHORT-CIRCUIT: right side not evaluated if result already determined.
     * &  and |  are NON-SHORT-CIRCUIT: both sides always evaluated.
     * ^  is XOR: true when operands differ.
     */
    public void logicalOperators() {
        boolean t = true, f = false;

        System.out.println("t && f = " + (t && f));  // false
        System.out.println("t || f = " + (t || f));  // true
        System.out.println("!t     = " + (!t));       // false
        System.out.println("t ^ f  = " + (t ^ f));   // true (XOR)
        System.out.println("t ^ t  = " + (t ^ t));   // false

        // Short-circuit: right side NOT evaluated when result is known
        int counter = 0;
        boolean result = false && (++counter > 0);    // counter stays 0
        System.out.println("Short-circuit &&, counter: " + counter); // 0

        result = true || (++counter > 0);             // counter stays 0
        System.out.println("Short-circuit ||, counter: " + counter); // 0

        // Non-short-circuit: BOTH sides always evaluated
        result = false & (++counter > 0);             // counter becomes 1
        System.out.println("Non-short-circuit &, counter: " + counter); // 1
    }


    // ── 6. Bitwise Operators ──────────────────────────────────────────────────
    /**
     * Operate on individual bits of integer types.
     *
     * <pre>
     *  &   — AND: bit is 1 only if both are 1
     *  |   — OR:  bit is 1 if either is 1
     *  ^   — XOR: bit is 1 if bits differ
     *  ~   — NOT: flips all bits (unary)
     *  <<  — left shift:  multiply by 2^n
     *  >>  — right shift: divide by 2^n (sign-preserving)
     *  >>> — unsigned right shift: shifts in zeros regardless of sign
     * </pre>
     */
    public void bitwiseOperators() {
        // 5  = 0000 0101
        // 3  = 0000 0011
        int a = 5, b = 3;

        System.out.println("5 & 3  = " + (a & b));   // 1  (0000 0001)
        System.out.println("5 | 3  = " + (a | b));   // 7  (0000 0111)
        System.out.println("5 ^ 3  = " + (a ^ b));   // 6  (0000 0110)
        System.out.println("~5     = " + (~a));       // -6 (flips all bits → -6 in two's complement)

        // Shift operators
        System.out.println("5 << 1  = " + (a << 1)); // 10  (5 * 2)
        System.out.println("5 << 2  = " + (a << 2)); // 20  (5 * 4)
        System.out.println("20 >> 2 = " + (20 >> 2));// 5   (20 / 4)

        // Signed vs unsigned right shift
        int negative = -8;
        System.out.println("-8 >> 1  = " + (negative >> 1));    // -4  (sign preserved)
        System.out.println("-8 >>> 1 = " + (negative >>> 1));   // large positive number

        // Common bitwise use: check if number is even/odd
        System.out.println("5 is odd: "  + ((5 & 1) == 1));     // true
        System.out.println("4 is even: " + ((4 & 1) == 0));     // true
    }


    // ── 7. Ternary Operator ───────────────────────────────────────────────────
    /**
     * Compact if-else for simple value selection.
     * Syntax: {@code condition ? valueIfTrue : valueIfFalse}
     *
     * <p>Best used for simple, readable one-liners.
     * Avoid nesting ternaries — it hurts readability fast.
     */
    public void ternaryOperator() {
        int score = 75;

        String grade = score >= 90 ? "A"
                     : score >= 80 ? "B"
                     : score >= 70 ? "C"
                     : "F";
        System.out.println("Score " + score + " → grade: " + grade); // C

        // Ternary in expressions
        int abs = score < 0 ? -score : score;
        System.out.println("abs(-75) via ternary: " + abs);

        // Ternary for null-safe default
        String name = null;
        String display = name != null ? name : "Anonymous";
        System.out.println("display: " + display); // Anonymous
    }


    // ── 8. Operator Precedence ────────────────────────────────────────────────
    /**
     * Higher precedence operators are evaluated first (like PEMDAS in math).
     *
     * <pre>
     *  Highest → Lowest (simplified):
     *  1. Postfix: x++, x--
     *  2. Prefix:  ++x, --x, !, ~, unary +/-
     *  3. Multiplicative: * / %
     *  4. Additive: + -
     *  5. Shift: << >> >>>
     *  6. Relational: < > <= >= instanceof
     *  7. Equality: == !=
     *  8. Bitwise AND: &
     *  9. Bitwise XOR: ^
     * 10. Bitwise OR:  |
     * 11. Logical AND: &&
     * 12. Logical OR:  ||
     * 13. Ternary: ?:
     * 14. Assignment: = += -= ...
     * </pre>
     *
     * <p><b>When in doubt: use parentheses.</b>
     */
    public void operatorPrecedence() {
        // * before +
        int result1 = 2 + 3 * 4;       // 14, not 20
        int result2 = (2 + 3) * 4;     // 20 — explicit grouping

        // Comparison vs equality
        boolean b1 = 2 + 3 == 5;       // true: (2+3)==5
        boolean b2 = 2 + 3 > 4 == true;// true: ((2+3)>4)==true

        // && before ||
        boolean b3 = true || false && false;  // true: true || (false&&false)
        boolean b4 = (true || false) && false;// false — different grouping

        System.out.println("2+3*4 = "               + result1); // 14
        System.out.println("(2+3)*4 = "             + result2); // 20
        System.out.println("2+3==5: "               + b1);      // true
        System.out.println("true||false&&false: "   + b3);      // true
        System.out.println("(true||false)&&false: " + b4);      // false
    }


    // ── Main — quick smoke run ─────────────────────────────────────────────────
    public static void main(String[] args) {
        OperatorsDemo demo = new OperatorsDemo();

        System.out.println("\n=== 1. Arithmetic ===");
        demo.arithmeticOperators();

        System.out.println("\n=== 2. Increment/Decrement ===");
        demo.incrementDecrement();

        System.out.println("\n=== 3. Compound Assignment ===");
        demo.compoundAssignment();

        System.out.println("\n=== 4. Comparison ===");
        demo.comparisonOperators();

        System.out.println("\n=== 5. Logical ===");
        demo.logicalOperators();

        System.out.println("\n=== 6. Bitwise ===");
        demo.bitwiseOperators();

        System.out.println("\n=== 7. Ternary ===");
        demo.ternaryOperator();

        System.out.println("\n=== 8. Precedence ===");
        demo.operatorPrecedence();
    }
}
