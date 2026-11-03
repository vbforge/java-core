package com.vbforge.completablefuture_and_async;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Practice: composing asynchronous work (see {@link ComposingDemo}).
 */
public class ComposingTasks {

    /**
     * Run {@code source} on another thread, then trim the text it produces and
     * convert it to upper case.
     *
     * <p>Examples:
     * <pre>
     * asyncTrimUpper(() -> "  hi ")  →  future of "HI"
     * asyncTrimUpper(() -> "Java")   →  future of "JAVA"
     * asyncTrimUpper(() -> "   ")    →  future of ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code source} must <b>not</b> run on the calling thread</li>
     *   <li>Start with the static factory that accepts a {@code Supplier}, then add a transforming stage</li>
     *   <li>If {@code source} throws, the returned future completes exceptionally</li>
     * </ul>
     *
     * @param source supplier of the raw text
     * @return a future of the trimmed, upper-cased text
     */
    public CompletableFuture<String> asyncTrimUpper(Supplier<String> source) {
        throw new UnsupportedOperationException("TODO: implement asyncTrimUpper");
    }

    /**
     * Look up a user's name by id, <b>then</b> look up that name's e-mail address.
     * The second lookup needs the result of the first, so it may only be started
     * once the first future has completed.
     *
     * <p>Examples:
     * <pre>
     * emailForUserId(7, id -> done("user-" + id), name -> done(name + "@example.com"))
     *     →  future of "user-7@example.com"        (done(x) = an already completed future of x)
     *
     * findName fails  →  the returned future fails, findEmail is never called
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Each lookup already returns a future — mapping with {@code thenApply} would leave you
     *       with a future inside a future</li>
     *   <li>Look for the stage that "flattens" a function returning a future</li>
     * </ul>
     *
     * @param userId    id to look up
     * @param findName  async lookup: user id → user name
     * @param findEmail async lookup: user name → e-mail address
     * @return a future of the e-mail address
     */
    public CompletableFuture<String> emailForUserId(int userId,
                                                    Function<Integer, CompletableFuture<String>> findName,
                                                    Function<String, CompletableFuture<String>> findEmail) {
        throw new UnsupportedOperationException("TODO: implement emailForUserId");
    }

    /**
     * Combine two independent futures into one description: {@code "<name> (<age>)"}.
     * The result completes only after <b>both</b> inputs have completed; if either
     * fails, the result fails.
     *
     * <p>Examples:
     * <pre>
     * describePerson(done("Alice"), done(30))  →  future of "Alice (30)"
     * describePerson(done("Bob"),   done(7))   →  future of "Bob (7)"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The two futures do not depend on each other — pick the stage that takes
     *       <i>another future</i> plus a two-argument function</li>
     * </ul>
     *
     * @param name future of the person's name
     * @param age  future of the person's age
     * @return a future of the combined text
     */
    public CompletableFuture<String> describePerson(CompletableFuture<String> name,
                                                    CompletableFuture<Integer> age) {
        throw new UnsupportedOperationException("TODO: implement describePerson");
    }

    /**
     * When {@code source} completes successfully, append {@code "done:" + value}
     * to {@code log}. The returned future completes <b>after</b> the log entry has
     * been written. If {@code source} fails, nothing is appended and the returned
     * future fails.
     *
     * <p>Examples:
     * <pre>
     * source completes with "hello"  →  log = ["done:hello"], returned future completes (value null)
     * source fails                   →  log = [],             returned future fails
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You only consume the value — no new value is produced</li>
     *   <li>Do not block with {@code join()}; attach a stage instead</li>
     * </ul>
     *
     * @param source future whose value is logged
     * @param log    thread-safe list that receives the log entry
     * @return a future that completes once the entry is written
     */
    public CompletableFuture<Void> recordWhenDone(CompletableFuture<String> source, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement recordWhenDone");
    }
}
