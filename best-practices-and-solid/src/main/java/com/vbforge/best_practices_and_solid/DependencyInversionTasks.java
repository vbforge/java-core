package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.DependencyInversionDemo.MessageSender;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * 🎯 TASKS — Dependency Inversion Principle
 *
 * <p>Implement every method so that all tests in {@link DependencyInversionTasksTest} pass.
 * Every collaborator arrives as a parameter — never call {@code new}, {@code Instant.now()}
 * or {@code LocalTime.now()} without a clock.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 * </ul>
 */
public class DependencyInversionTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Send {@code message} to every recipient through the given sender and return the
     * receipts in the same order as the recipients.
     *
     * <p>Examples:
     * <pre>
     *   sender = (to, msg) -> to + "|" + msg
     *   notifyAll(["a", "b"], "hi", sender)  → ["a|hi", "b|hi"]
     *   notifyAll([], "hi", sender)          → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You only know the {@link MessageSender} abstraction — never its implementations</li>
     * </ul>
     *
     * @param recipients who gets the message
     * @param message    the text to send
     * @param sender     the delivery channel
     * @return one receipt per recipient, in order
     */
    public List<String> notifyAll(List<String> recipients, String message, MessageSender sender) {
        throw new UnsupportedOperationException("TODO: implement notifyAll");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Decide whether something has expired. It is expired when the clock's current
     * instant is <b>not before</b> {@code expiresAt} (so exactly at the expiry moment
     * counts as expired).
     *
     * <p>Examples (expiresAt = 2024-01-01T12:00:00Z):
     * <pre>
     *   clock at 11:59:59Z  → false
     *   clock at 12:00:00Z  → true
     *   clock at 12:00:01Z  → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Clock.instant()} is "now" as seen by that clock</li>
     *   <li>{@code Instant.isBefore} / {@code isAfter} compare instants</li>
     * </ul>
     *
     * @param expiresAt the moment the item expires
     * @param clock     the source of "now"
     * @return {@code true} if expired
     */
    public boolean isExpired(Instant expiresAt, Clock clock) {
        throw new UnsupportedOperationException("TODO: implement isExpired");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Pick a greeting from the hour of day as seen by the given clock (in the clock's
     * own time zone):
     * <pre>
     *   05:00–11:59  → "Good morning"
     *   12:00–17:59  → "Good afternoon"
     *   18:00–04:59  → "Good evening"
     * </pre>
     *
     * <p>Examples (clock fixed in UTC):
     * <pre>
     *   09:00 → "Good morning"
     *   12:00 → "Good afternoon"
     *   23:30 → "Good evening"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code ZonedDateTime.now(clock)} or {@code LocalTime.now(clock)} reads time from the clock you were given</li>
     * </ul>
     *
     * @param clock the source of "now" (and the time zone)
     * @return the greeting
     */
    public String greeting(Clock clock) {
        throw new UnsupportedOperationException("TODO: implement greeting");
    }
}
