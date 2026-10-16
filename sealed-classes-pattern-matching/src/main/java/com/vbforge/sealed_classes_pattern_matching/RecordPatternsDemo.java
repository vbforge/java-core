package com.vbforge.sealed_classes_pattern_matching;

/**
 * Record patterns (Java 21) combined with sealed types: algebraic data types in Java.
 *
 * <p>A <b>record pattern</b> tests the type AND pulls the record's components out
 * in one step: {@code obj instanceof Point(int x, int y)}.
 *
 * <p>Sections:
 * <ol>
 *   <li>Record pattern basics</li>
 *   <li>Nested record patterns (and what they do with {@code null})</li>
 *   <li>Sealed interface + records = exhaustive evaluator</li>
 *   <li>Generic records and {@code Result<T>}</li>
 * </ol>
 *
 * <p>Complexity: {@code eval}, {@code show} and {@code simplify} visit each node of the
 * tree a constant number of times — O(n) for n nodes (recursion depth = tree depth).
 */
public class RecordPatternsDemo {

    // ── 1. Record pattern basics ──────────────────────────────────────────────
    /**
     * What: deconstruct a record into its components directly in the pattern.
     *
     * <p>Why: no accessor calls ({@code p.x()}, {@code p.y()}) and no temporary variable.
     *
     * <p>Gotcha: the pattern's component list must match the record header exactly.
     */
    public record Point(int x, int y) { }

    public record Line(Point from, Point to) { }

    public static String describePoint(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            return "Point at (" + x + ", " + y + ")";
        }
        return "not a point";
    }

    public static void recordPatternBasicsDemo() {
        System.out.println(describePoint(new Point(3, 4)));
        System.out.println(describePoint("text"));
    }


    // ── 2. Nested record patterns ─────────────────────────────────────────────
    /**
     * What: record patterns nest — {@code Line(Point(var x1, var y1), Point(var x2, var y2))}
     * reaches four ints in one expression. {@code var} infers each component type.
     *
     * <p>Why: it mirrors the shape of the data you are inspecting.
     *
     * <p>Gotcha: a NESTED record pattern does not match {@code null}, but a
     * {@code var} / total type pattern does. So a Line whose {@code from} is null
     * fails {@code Point(var x, var y)} yet still matches {@code var from}.
     */
    public static boolean isHorizontal(Object obj) {
        return obj instanceof Line(Point(var x1, var y1), Point(var x2, var y2))
                && y1 == y2 && x1 != x2;
    }

    public static String startOf(Line line) {
        return switch (line) {
            case Line(Point(var x, var y), var to) -> "starts at (" + x + ", " + y + ")";
            case Line(var from, var to) -> "no start point";
        };
    }

    public static void nestedRecordPatternsDemo() {
        System.out.println(isHorizontal(new Line(new Point(0, 2), new Point(5, 2))));
        System.out.println(startOf(new Line(new Point(1, 1), new Point(2, 2))));
        System.out.println(startOf(new Line(null, new Point(2, 2))));
    }


    // ── 3. Sealed interface + records = exhaustive evaluator ──────────────────
    /**
     * What: a sealed interface whose permitted types are records models "one of these
     * shapes of data" — an algebraic data type.
     *
     * <p>Why: every operation is one exhaustive switch with no {@code default}; adding
     * a new Expr variant breaks the build everywhere it must be handled.
     *
     * <p>Gotcha in {@code simplify}: order matters. Specific / guarded cases come
     * before the general one for the same record, e.g. {@code Neg(Neg(x))} before
     * {@code Neg(var o)}.
     */
    public sealed interface Expr permits Num, Add, Mul, Neg { }

    public record Num(int value) implements Expr { }

    public record Add(Expr left, Expr right) implements Expr { }

    public record Mul(Expr left, Expr right) implements Expr { }

    public record Neg(Expr operand) implements Expr { }

    public static int eval(Expr expr) {
        return switch (expr) {
            case Num(var v) -> v;
            case Add(var l, var r) -> eval(l) + eval(r);
            case Mul(var l, var r) -> eval(l) * eval(r);
            case Neg(var o) -> -eval(o);
        };
    }

    public static String show(Expr expr) {
        return switch (expr) {
            case Num(var v) -> String.valueOf(v);
            case Add(var l, var r) -> "(" + show(l) + " + " + show(r) + ")";
            case Mul(var l, var r) -> "(" + show(l) + " * " + show(r) + ")";
            case Neg(var o) -> "-" + show(o);
        };
    }

    /** One bottom-up-ish pass: folds constants, drops +0 and *1, collapses double negation. */
    public static Expr simplify(Expr expr) {
        return switch (expr) {
            case Num n -> n;
            case Neg(Neg(var inner)) -> simplify(inner);
            case Neg(Num(var v)) -> new Num(-v);
            case Neg(var o) -> new Neg(simplify(o));
            case Add(Num(var a), Num(var b)) -> new Num(a + b);
            case Add(var l, Num(var v)) when v == 0 -> simplify(l);
            case Add(Num(var v), var r) when v == 0 -> simplify(r);
            case Add(var l, var r) -> new Add(simplify(l), simplify(r));
            case Mul(Num(var a), Num(var b)) -> new Num(a * b);
            case Mul(var l, Num(var v)) when v == 0 -> new Num(0);
            case Mul(Num(var v), var r) when v == 0 -> new Num(0);
            case Mul(var l, Num(var v)) when v == 1 -> simplify(l);
            case Mul(Num(var v), var r) when v == 1 -> simplify(r);
            case Mul(var l, var r) -> new Mul(simplify(l), simplify(r));
        };
    }

    public static void sealedAndRecordsDemo() {
        Expr e = new Add(new Num(2), new Mul(new Num(3), new Num(4)));
        System.out.println(show(e) + " = " + eval(e));
        System.out.println(show(simplify(new Add(new Num(0), new Mul(new Num(1), new Num(7))))));
    }


    // ── 4. Generic records and Result<T> ──────────────────────────────────────
    /**
     * What: record patterns work with generic records; type arguments are inferred.
     *
     * <p>Why: {@code Result<T>} (success or failure) makes "this can fail" part of the
     * type, instead of returning null or throwing.
     */
    public sealed interface Result<T> permits Success, Failure { }

    public record Success<T>(T value) implements Result<T> { }

    public record Failure<T>(String error) implements Result<T> { }

    public static Result<Integer> parse(String text) {
        try {
            return new Success<>(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return new Failure<>("not a number: " + text);
        }
    }

    public static <T> String render(Result<T> result) {
        return switch (result) {
            case Success<T>(var value) -> "ok: " + value;
            case Failure<T>(var error) -> "error: " + error;
        };
    }

    public static void genericRecordPatternsDemo() {
        System.out.println(render(parse("42")));
        System.out.println(render(parse("abc")));
    }


    public static void main(String[] args) {
        recordPatternBasicsDemo();
        System.out.println("---");
        nestedRecordPatternsDemo();
        System.out.println("---");
        sealedAndRecordsDemo();
        System.out.println("---");
        genericRecordPatternsDemo();
    }
}
