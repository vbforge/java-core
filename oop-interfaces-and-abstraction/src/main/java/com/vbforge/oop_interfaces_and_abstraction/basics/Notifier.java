package com.vbforge.oop_interfaces_and_abstraction.basics;

/**
 * A contract for anything that can deliver a message.
 *
 * <p>Everything declared in an interface is implicitly {@code public}: the constant is
 * {@code public static final} and {@link #send(String)} is {@code public abstract}.
 */
public interface Notifier {

    /** Longest message an SMS-like channel delivers in full (implicitly {@code public static final}). */
    int MAX_LENGTH = 20;

    /**
     * Delivers {@code message} and returns a receipt describing what was sent
     * (implicitly {@code public abstract}).
     */
    String send(String message);
}
