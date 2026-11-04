package com.vbforge.reflection_api;

import java.util.Map;

/**
 * Tasks for JDK dynamic proxies.
 *
 * <p>Read {@link DynamicProxyDemo} first. The fixtures used in the examples
 * ({@code Calculator}, {@code Repository}) are declared there.
 *
 * <p>In every task the proxy must be <em>invisible</em>: if the real method throws a
 * {@code RuntimeException}, the caller must receive that very exception — not an
 * {@code InvocationTargetException} and not an {@code UndeclaredThrowableException}.
 */
public class DynamicProxyTasks {

    /**
     * Wrap {@code target} in a proxy that counts how many times each method is called.
     * The counter is keyed by method <b>name</b> and increased <b>before</b> delegating, so a call
     * that throws is still counted. Every call is delegated to {@code target} and its result returned.
     *
     * <p>Examples:
     * <pre>
     *   Calculator calc = countingProxy(new SimpleCalculator(), Calculator.class, counters);
     *   calc.add(1, 2); calc.add(3, 4); calc.name();
     *       → counters == {add=2, name=1}
     *   calc.divide(1, 0)           → throws ArithmeticException; counters has divide=1
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Proxy.newProxyInstance} needs a class loader, the interfaces and a handler
     *       (a lambda works — {@code InvocationHandler} has one abstract method)</li>
     *   <li>{@code Map.merge} is a compact way to increment a counter</li>
     *   <li>Remember what you learned about {@code InvocationTargetException}</li>
     * </ul>
     *
     * @param target   the real object
     * @param iface    the interface to expose
     * @param counters map that receives {@code methodName → number of calls}
     * @param <T>      the interface type
     * @return a proxy implementing {@code iface}
     */
    public <T> T countingProxy(T target, Class<T> iface, Map<String, Integer> counters) {
        throw new UnsupportedOperationException("TODO: implement countingProxy");
    }

    /**
     * Wrap {@code target} in a <b>memoizing</b> proxy: the first call with a given method and
     * given argument values runs the real method; later calls with the same method and
     * {@code equals} arguments return the cached result without touching {@code target}.
     * Exceptions are <b>not</b> cached — a call that throws is retried on the next invocation.
     *
     * <p>Examples:
     * <pre>
     *   Calculator calc = memoizingProxy(realCalc, Calculator.class);
     *   calc.add(2, 3); calc.add(2, 3);     → realCalc.add executed once, both return 5
     *   calc.add(3, 2);                     → executed (different arguments)
     *   calc.name(); calc.name();           → executed once   (no-arg methods work too)
     *   calc.divide(1, 0); calc.divide(1, 0) → ArithmeticException twice, executed twice
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need a cache key built from the {@code Method} and the argument values</li>
     *   <li>{@code args} is {@code null} for methods without parameters</li>
     *   <li>{@code Arrays.asList(...)} and {@code Arrays.deepToString(...)} both produce usable keys —
     *       think about which one copes with {@code null}</li>
     * </ul>
     *
     * @param target the real object
     * @param iface  the interface to expose
     * @param <T>    the interface type
     * @return a caching proxy implementing {@code iface}
     */
    public <T> T memoizingProxy(T target, Class<T> iface) {
        throw new UnsupportedOperationException("TODO: implement memoizingProxy");
    }

    /**
     * Return a <b>read-only view</b> of {@code target}: any method whose name starts with
     * {@code put}, {@code add}, {@code set}, {@code remove} or {@code clear} must throw
     * {@code UnsupportedOperationException} <em>without</em> reaching {@code target}.
     * All other methods are delegated normally.
     *
     * <p>Examples:
     * <pre>
     *   Repository view = readOnlyView(repo, Repository.class);
     *   view.get("a")          → delegates, returns repo's value
     *   view.put("a", "1")     → UnsupportedOperationException, repo unchanged
     *   view.remove("a")       → UnsupportedOperationException, repo unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code method.getName()} gives you the name to test</li>
     *   <li>{@code String.startsWith} is enough — no regular expression needed</li>
     * </ul>
     *
     * @param target the real object
     * @param iface  the interface to expose
     * @param <T>    the interface type
     * @return a proxy that rejects mutating calls
     */
    public <T> T readOnlyView(T target, Class<T> iface) {
        throw new UnsupportedOperationException("TODO: implement readOnlyView");
    }
}
