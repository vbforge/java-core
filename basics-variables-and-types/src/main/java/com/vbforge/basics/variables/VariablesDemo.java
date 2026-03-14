package com.vbforge.basics.variables;

/**
 * Demonstrates Java primitive types, type casting, literals,
 * constants, variable scope, and var type inference.
 *
 * <p>This class is intentionally structured as a learning reference:
 * each method isolates one concept so examples stay clear and focused.
 *
 * <p>Java versions covered: 8 (primitives, casting) → 10 (var) → 15 (text blocks) → 17 (sealed preview)
 */
public class VariablesDemo {

    // ── Constants ──────────────────────────────────────────────────────────────
    // 'static final' = compile-time constant. By convention: UPPER_SNAKE_CASE.
    // 'final' alone = effectively constant but instance-level.
    public static final int    MAX_RETRY_COUNT  = 3;
    public static final double PI_APPROXIMATION = 3.14159265358979;
    public static final String APP_NAME         = "java-core";


    // ── 1. Primitive Types ─────────────────────────────────────────────────────
    /**
     * Java has exactly 8 primitive types. They are stored on the stack (when local)
     * and hold the value directly — no object overhead.
     *
     * <pre>
     *  Type     | Size    | Range / Notes
     *  ---------|---------|--------------------------------------------
     *  byte     | 8-bit   | -128 to 127
     *  short    | 16-bit  | -32,768 to 32,767
     *  int      | 32-bit  | ~-2.1B to 2.1B  (default integer literal)
     *  long     | 64-bit  | ~-9.2E18 to 9.2E18  (suffix: L)
     *  float    | 32-bit  | ~7 decimal digits   (suffix: f)
     *  double   | 64-bit  | ~15 decimal digits  (default float literal)
     *  char     | 16-bit  | Unicode 0 to 65,535 (single quotes)
     *  boolean  | 1-bit*  | true / false only   (*JVM-dependent size)
     * </pre>
     */
    public void primitiveTypes() {
        byte    smallNumber  = 127;
        short   mediumNumber = 32_000;       // underscores improve readability (Java 7+)
        int     regularInt   = 2_147_483_647;
        long    bigNumber    = 9_223_372_036_854_775_807L; // L suffix required

        float   singlePrecision = 3.14f;     // f suffix required
        double  doublePrecision = 3.141592653589793;

        char    letter    = 'A';
        char    unicode   = '\u0041';        // also 'A' — unicode escape
        boolean flag      = true;

        // Numeric literals: different bases
        int decimal     = 255;
        int hex         = 0xFF;             // 255 in hexadecimal
        int binary      = 0b11111111;       // 255 in binary (Java 7+)
        int octal       = 0377;             // 255 in octal

        System.out.println("byte max: "   + smallNumber);
        System.out.println("long max: "   + bigNumber);
        System.out.println("hex 0xFF: "   + hex);
        System.out.println("binary: "     + binary);
        System.out.println("char 'A': "   + letter + " = unicode " + (int) letter);
    }


    // ── 2. Type Casting ────────────────────────────────────────────────────────
    /**
     * Widening (implicit): smaller → larger type. Safe, no data loss.
     * Narrowing (explicit): larger → smaller type. Requires cast, possible data loss.
     */
    public void typeCasting() {
        // Widening — automatic, safe
        int    intVal    = 42;
        long   longVal   = intVal;           // int → long: widening, implicit
        double doubleVal = longVal;          // long → double: widening, implicit

        // Narrowing — explicit cast required
        double pi       = 3.99;
        int    truncated = (int) pi;         // → 3, decimal part is TRUNCATED (not rounded!)

        long   bigLong   = 1_000_000_000_000L;
        int    overflow  = (int) bigLong;    // data loss — bits are cut off

        // char ↔ int casting
        char   ch      = 'A';
        int    ascii   = ch;                 // widening: char → int
        char   back    = (char) (ascii + 1); // narrowing: int → char = 'B'

        System.out.println("double 3.99 cast to int: " + truncated);  // 3
        System.out.println("long overflow to int: "    + overflow);
        System.out.println("'A' + 1 as char: "         + back);       // B
    }


    // ── 3. Wrapper Types & Autoboxing ──────────────────────────────────────────
    /**
     * Each primitive has a corresponding wrapper class (java.lang package).
     * Autoboxing: compiler auto-converts primitive ↔ wrapper.
     *
     * <pre>
     *  Primitive → Wrapper
     *  int       → Integer
     *  long      → Long
     *  double    → Double
     *  boolean   → Boolean
     *  char      → Character
     *  ... etc
     * </pre>
     *
     * Important: Integer caches values -128 to 127. Outside this range,
     * == comparison returns false even for equal values!
     */
    public void wrapperTypesAndAutoboxing() {
        // Autoboxing: int → Integer (implicit)
        Integer boxed = 42;
        int     unboxed = boxed;             // unboxing: Integer → int (implicit)

        // Wrapper utility methods
        int    parsed   = Integer.parseInt("123");
        String asString = Integer.toString(456);
        int    maxInt   = Integer.MAX_VALUE;
        int    minInt   = Integer.MIN_VALUE;

        // ⚠️ Integer cache trap
        Integer a = 127;
        Integer b = 127;
        Integer x = 128;
        Integer y = 128;

        System.out.println("127 == 127: " + (a == b));   // true  (cached)
        System.out.println("128 == 128: " + (x == y));   // false (NOT cached!)
        System.out.println("128 equals: " + x.equals(y));// true  (always use equals!)

        System.out.println("Parsed: " + parsed);
        System.out.println("MAX_VALUE: " + maxInt);
    }


    // ── 4. Variable Scope ──────────────────────────────────────────────────────
    /**
     * Scope defines where a variable is accessible.
     *
     * <ul>
     *   <li><b>Class (static)</b> — lives as long as the class is loaded</li>
     *   <li><b>Instance</b>       — one per object instance</li>
     *   <li><b>Local</b>          — only within the declaring block/method</li>
     *   <li><b>Block</b>          — only within the { } where declared</li>
     * </ul>
     */
    private static int classVariable = 0;   // class scope
    private        int instanceVar   = 0;   // instance scope

    public void variableScope() {
        int localVar = 10;                  // local scope — only in this method

        if (localVar > 5) {
            int blockVar = 20;              // block scope — only inside this if
            System.out.println("blockVar visible here: " + blockVar);
        }
        // blockVar not accessible here — compile error if uncommented:
        // System.out.println(blockVar);

        for (int i = 0; i < 3; i++) {      // 'i' is scoped to the for loop
            System.out.println("loop i: " + i);
        }
        // 'i' not accessible here

        classVariable++;
        instanceVar++;
        System.out.println("local: " + localVar + ", instance: " + instanceVar + ", class: " + classVariable);
    }


    // ── 5. var — Local Variable Type Inference (Java 10+) ─────────────────────
    /**
     * {@code var} lets the compiler infer the type from the right-hand side.
     * It is NOT dynamic typing — the type is fixed at compile time.
     *
     * <p>Rules:
     * <ul>
     *   <li>Only for LOCAL variables with an initializer</li>
     *   <li>Cannot be used for fields, method parameters, or return types</li>
     *   <li>Cannot be initialized to null (type would be unknown)</li>
     * </ul>
     */
    public void varTypeInference() {
        // With explicit types
        String  explicitString = "Hello";
        int     explicitInt    = 42;

        // Same with var — compiler infers the type
        var inferredString = "Hello";       // inferred as String
        var inferredInt    = 42;            // inferred as int
        var inferredDouble = 3.14;          // inferred as double

        // var works great with long generic types
        var list = new java.util.ArrayList<String>();
        list.add("Java");
        list.add("Core");

        // ⚠️ var cannot be used here (would not compile):
        // var noInit;              // no initializer
        // var nullVal = null;      // type unknown

        System.out.println("var string: " + inferredString.toUpperCase()); // String methods available
        System.out.println("var list: "   + list);
        System.out.println("var double: " + inferredDouble);
    }


    // ── 6. final variables ────────────────────────────────────────────────────
    /**
     * {@code final} means the variable reference cannot be reassigned after initialization.
     * For primitives: value is constant.
     * For objects: the reference is constant, but the object's internal state can still change.
     */
    public void finalVariables() {
        final int CONSTANT = 100;
        // CONSTANT = 200; // ← compile error: cannot reassign final variable

        final var list = new java.util.ArrayList<String>();
        list.add("still works");            // ✓ modifying content is fine
        // list = new java.util.ArrayList<>(); // ← compile error: cannot reassign

        // Blank final — must be assigned exactly once before use
        final int blankFinal;
        blankFinal = 42;                    // assigned exactly once
        // blankFinal = 43;                 // ← compile error

        System.out.println("final constant: " + CONSTANT);
        System.out.println("final list: "     + list);
        System.out.println("blank final: "    + blankFinal);
    }


    // ── Main — quick smoke run ─────────────────────────────────────────────────
    public static void main(String[] args) {
        VariablesDemo demo = new VariablesDemo();

        System.out.println("\n=== 1. Primitive Types ===");
        demo.primitiveTypes();

        System.out.println("\n=== 2. Type Casting ===");
        demo.typeCasting();

        System.out.println("\n=== 3. Wrapper Types & Autoboxing ===");
        demo.wrapperTypesAndAutoboxing();

        System.out.println("\n=== 4. Variable Scope ===");
        demo.variableScope();

        System.out.println("\n=== 5. var Type Inference ===");
        demo.varTypeInference();

        System.out.println("\n=== 6. Final Variables ===");
        demo.finalVariables();
    }
}
