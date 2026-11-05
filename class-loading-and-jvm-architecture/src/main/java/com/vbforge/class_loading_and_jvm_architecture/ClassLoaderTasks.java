package com.vbforge.class_loading_and_jvm_architecture;

/**
 * Practice tasks for class loaders. Read {@link ClassLoaderDemo} first.
 */
public class ClassLoaderTasks {

    /**
     * Count the class loaders between a class and the bootstrap loader.
     * The bootstrap loader itself is NOT counted, so bootstrap classes have depth 0.
     *
     * <p>Examples:
     * <pre>
     * loaderDepth(String.class)     → 0   (bootstrap)
     * loaderDepth(int.class)        → 0   (primitives report no loader)
     * loaderDepth(String[].class)   → 0   (arrays use their element type's loader)
     * loaderDepth(MyApp.class)      → 2 or more (app loader + its ancestors)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Class.getClassLoader()} returns {@code null} for the bootstrap loader.</li>
     *   <li>{@code ClassLoader.getParent()} walks one step up.</li>
     * </ul>
     *
     * @param type any class, interface, array or primitive type
     * @return number of non-bootstrap loaders in the chain
     */
    public int loaderDepth(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement loaderDepth");
    }

    /**
     * Describe the loader that defined a class.
     * Return {@code "<bootstrap>"} when there is no loader object,
     * otherwise the fully-qualified name of the loader's own class.
     *
     * <p>Examples:
     * <pre>
     * definingLoaderName(String.class)        → "&lt;bootstrap&gt;"
     * definingLoaderName(int.class)           → "&lt;bootstrap&gt;"
     * definingLoaderName(MyApp.class)         → name of the application loader's class
     * definingLoaderName(isolatedGreeter)     → "...ClassLoaderDemo$IsolatingClassLoader"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check for {@code null} before calling anything on the loader.</li>
     *   <li>You want the class of the loader object, not the class you were given.</li>
     * </ul>
     *
     * @param type any class or primitive type
     * @return {@code "<bootstrap>"} or the loader's class name
     */
    public String definingLoaderName(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement definingLoaderName");
    }

    /**
     * Decide whether two objects are instances of exactly the same runtime class.
     * Two classes with the same name but different defining loaders are NOT the same class.
     * If either argument is {@code null}, return {@code false}.
     *
     * <p>Examples:
     * <pre>
     * isSameRuntimeType("a", "b")                  → true
     * isSameRuntimeType("a", 42)                   → false
     * isSameRuntimeType(new Greeter(), isolated)   → false  (same name, different loader)
     * isSameRuntimeType(null, "a")                 → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A class's identity is its name <em>and</em> its defining loader.</li>
     *   <li>Comparing names alone gives the wrong answer for the third example.</li>
     * </ul>
     *
     * @param a first object, may be null
     * @param b second object, may be null
     * @return true if both are non-null and share one runtime class
     */
    public boolean isSameRuntimeType(Object a, Object b) {
        throw new UnsupportedOperationException("TODO: implement isSameRuntimeType");
    }
}
