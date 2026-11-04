package com.vbforge.annotations;

import java.util.List;

public class BuiltInAnnotationsTasks {

    /**
     * Checks whether a public method is annotated with {@code @Deprecated}.
     *
     * <p>Examples:
     * <pre>
     * isDeprecatedMethod(Calculator.class, "sum", int.class, int.class)      -> true
     * isDeprecatedMethod(Calculator.class, "add", int.class, int.class)      -> false
     * isDeprecatedMethod(Calculator.class, "missing", int.class)             -> throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Class.getMethod(String, Class...)} finds a public method by name and parameter types</li>
     *   <li>{@code @Deprecated} is retained at runtime, so it is visible to reflection</li>
     *   <li>{@code getMethod} reports a missing method with a checked exception — callers expect an unchecked one</li>
     * </ul>
     *
     * @param type           the class to inspect
     * @param methodName     the name of the public method
     * @param parameterTypes the parameter types of the method (none for a no-arg method)
     * @return {@code true} if the method carries {@code @Deprecated}
     * @throws IllegalArgumentException if the class has no such public method
     */
    public boolean isDeprecatedMethod(Class<?> type, String methodName, Class<?>... parameterTypes) {
        throw new UnsupportedOperationException("TODO: implement isDeprecatedMethod");
    }

    /**
     * Lists the names of all methods <b>declared by</b> the class (any visibility, not inherited)
     * that are annotated with {@code @Deprecated}, sorted alphabetically.
     *
     * <p>Examples:
     * <pre>
     * deprecatedMethodNames(Calculator.class) -> ["legacyMultiply", "sum"]
     * deprecatedMethodNames(Dog.class)        -> []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getMethods()} includes inherited members; {@code getDeclaredMethods()} does not</li>
     *   <li>The order of {@code getDeclaredMethods()} is unspecified — sort the result</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return alphabetically sorted method names; empty list when nothing is deprecated
     */
    public List<String> deprecatedMethodNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement deprecatedMethodNames");
    }

    /**
     * Decides whether an interface has the <i>shape</i> of a functional interface: exactly one
     * abstract method. Default methods and static methods do not count. Inherited abstract
     * methods do count. You may assume the interface does not re-declare methods of {@code Object}.
     *
     * <p>Examples:
     * <pre>
     * isStructurallyFunctional(Runnable.class)                  -> true   (run)
     * isStructurallyFunctional(Transformer.class)               -> true   (apply + default + static)
     * isStructurallyFunctional(java.util.List.class)            -> false  (many abstract methods)
     * isStructurallyFunctional(java.io.Serializable.class)      -> false  (marker, none)
     * isStructurallyFunctional(String.class)                    -> false  (not an interface)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Class.isInterface()} first — a class is never a functional interface</li>
     *   <li>{@code Class.getMethods()} on an interface includes methods from super-interfaces</li>
     *   <li>{@code java.lang.reflect.Modifier.isAbstract(int)} on {@code Method.getModifiers()}</li>
     * </ul>
     *
     * @param type the type to inspect
     * @return {@code true} if {@code type} is an interface with exactly one abstract method
     */
    public boolean isStructurallyFunctional(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement isStructurallyFunctional");
    }

    /**
     * Detects the classic {@code @Override} trap: does the class <b>really override</b>
     * {@code equals(Object)}, or does it only declare an overload such as {@code equals(MyType)}?
     * Only the class itself is inspected, not its superclasses.
     *
     * <p>Examples:
     * <pre>
     * overridesObjectEquals(FixedPoint.class)  -> true   (declares equals(Object))
     * overridesObjectEquals(BrokenPoint.class) -> false  (declares equals(BrokenPoint) only)
     * overridesObjectEquals(Calculator.class)  -> false  (declares no equals at all)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getDeclaredMethod(name, parameterTypes)} matches the exact parameter list</li>
     *   <li>A missing method is reported with {@code NoSuchMethodException}</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return {@code true} if the class itself declares {@code equals(Object)}
     */
    public boolean overridesObjectEquals(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement overridesObjectEquals");
    }
}
