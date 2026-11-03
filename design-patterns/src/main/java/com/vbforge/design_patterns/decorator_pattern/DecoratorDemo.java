package com.vbforge.design_patterns.decorator_pattern;

/**
 * DecoratorDemo — add behaviour to an object by wrapping it.
 *
 * <p>Topics:
 * <ol>
 *   <li>The base (identity) processor</li>
 *   <li>One decorator adds a single behaviour</li>
 *   <li>Chaining decorators — order matters</li>
 * </ol>
 */
public class DecoratorDemo {

    public static void main(String[] args) {
        baseDemo();
        System.out.println("---");
        singleDecoratorDemo();
        System.out.println("---");
        chainingDemo();
    }

    /**
     * Base — the "do nothing" processor.
     *
     * <p><b>Why:</b> every decorator must ultimately delegate to something.
     * The base gives the chain a place to bottom out.
     */
    static void baseDemo() {
        System.out.println("=== Base ===");
        TextProcessor p = s -> s;
        System.out.println("'" + p.process("hello") + "'");
    }

    /**
     * One decorator, one behaviour — uppercase.
     *
     * <p><b>Why:</b> decorator implements the same interface as the wrapped
     * object, so callers cannot tell the difference.
     */
    static void singleDecoratorDemo() {
        System.out.println("=== One decorator ===");
        TextProcessor base = s -> s;
        TextProcessor upper = s -> base.process(s).toUpperCase();
        System.out.println("'" + upper.process("hi") + "'");
    }

    /**
     * Chaining decorators — each layer wraps the previous.
     *
     * <p><b>Gotcha:</b> order of composition changes the result. Trim-then-
     * uppercase != uppercase-then-trim when the input has whitespace inside.
     */
    static void chainingDemo() {
        System.out.println("=== Chaining ===");
        TextProcessor base = s -> s;
        TextProcessor upper = s -> base.process(s).toUpperCase();
        TextProcessor trim  = s -> upper.process(s).trim();
        System.out.println("trim(upper): '" + trim.process("  hi  ") + "'");

        TextProcessor trimThenUpper = s -> base.process(s).trim().toUpperCase();
        System.out.println("upper(trim): '" + trimThenUpper.process("  hi  ") + "'");
    }

    @FunctionalInterface
    public interface TextProcessor {
        String process(String input);
    }
}