package com.vbforge.sealed_classes_pattern_matching;

import java.util.function.Function;

/**
 * 🎯 TASKS — Record patterns with sealed types
 *
 * <p>Implement every method so that {@code RecordPatternsTasksTest} passes.
 * Remove the {@code @Disabled} annotation from one nested test class at a time.
 * Do NOT change method signatures.
 */
public class RecordPatternsTasks {

    /**
     * Count every node of an expression tree (numbers AND operators).
     *
     * <p>Examples:
     * <pre>
     *   countNodes(Num(5))                      → 1
     *   countNodes(Neg(Num(5)))                 → 2
     *   countNodes(Add(Num(1), Mul(Num(2), Num(3)))) → 5
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An exhaustive {@code switch} with record patterns, no {@code default}</li>
     *   <li>Recurse into the sub-expressions</li>
     * </ul>
     *
     * @param expr the expression
     * @return number of nodes
     */
    public int countNodes(RecordPatternsDemo.Expr expr) {
        throw new UnsupportedOperationException("TODO: implement countNodes");
    }

    /**
     * Write an expression in postfix (reverse Polish) notation, tokens separated by one space.
     * Operators are written {@code +}, {@code *} and {@code neg}, after their operand(s).
     *
     * <p>Examples:
     * <pre>
     *   toPostfix(Num(4))                            → "4"
     *   toPostfix(Neg(Num(4)))                       → "4 neg"
     *   toPostfix(Add(Num(1), Mul(Num(2), Num(3))))  → "1 2 3 * +"
     *   toPostfix(Mul(Add(Num(1), Num(2)), Num(3)))  → "1 2 + 3 *"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Left subtree, then right subtree, then the operator</li>
     * </ul>
     *
     * @param expr the expression
     * @return the postfix string
     */
    public String toPostfix(RecordPatternsDemo.Expr expr) {
        throw new UnsupportedOperationException("TODO: implement toPostfix");
    }

    /**
     * Classify a line given as an arbitrary object.
     *
     * <p>Rules:
     * <pre>
     *   same start and end point        → "point"
     *   same y, different x             → "horizontal"
     *   same x, different y             → "vertical"
     *   otherwise                       → "diagonal"
     *   not a Line, null, or a Line with a null Point → "not a line"
     * </pre>
     *
     * <p>Examples:
     * <pre>
     *   lineKind(Line(Point(0, 2), Point(5, 2)))  → "horizontal"
     *   lineKind(Line(Point(1, 0), Point(1, 9)))  → "vertical"
     *   lineKind(Line(Point(0, 0), Point(3, 3)))  → "diagonal"
     *   lineKind("text")                          → "not a line"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One nested record pattern extracts all four coordinates</li>
     *   <li>Think about what a nested record pattern does with a null component</li>
     * </ul>
     *
     * @param obj any object
     * @return the kind as shown above
     */
    public String lineKind(Object obj) {
        throw new UnsupportedOperationException("TODO: implement lineKind");
    }

    /**
     * Get the value of a successful result, or the fallback for a failure.
     *
     * <p>Examples:
     * <pre>
     *   unwrapOr(Success(10), 0)        → 10
     *   unwrapOr(Failure("boom"), 0)    → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Exhaustive switch over the sealed {@code Result<T>}</li>
     * </ul>
     *
     * @param result a success or a failure
     * @param fallback value to return for a failure
     * @param <T> the value type
     * @return the success value, or {@code fallback}
     */
    public <T> T unwrapOr(RecordPatternsDemo.Result<T> result, T fallback) {
        throw new UnsupportedOperationException("TODO: implement unwrapOr");
    }

    /**
     * Transform the value inside a successful result. A failure stays a failure with
     * the same error message, and the mapper is NOT called.
     *
     * <p>Examples:
     * <pre>
     *   mapResult(Success(5), x -> x * 2)     → Success(10)
     *   mapResult(Success(5), x -> "n" + x)   → Success("n5")
     *   mapResult(Failure("boom"), x -> x * 2)→ Failure("boom")
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You must build a new {@code Failure} — its type argument changes from T to R</li>
     * </ul>
     *
     * @param result a success or a failure
     * @param mapper function applied to the success value
     * @param <T> input value type
     * @param <R> output value type
     * @return a new result
     */
    public <T, R> RecordPatternsDemo.Result<R> mapResult(
            RecordPatternsDemo.Result<T> result, Function<? super T, ? extends R> mapper) {
        throw new UnsupportedOperationException("TODO: implement mapResult");
    }
}
