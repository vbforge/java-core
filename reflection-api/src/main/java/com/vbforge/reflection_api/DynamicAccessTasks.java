package com.vbforge.reflection_api;

import java.util.Map;

/**
 * Tasks for dynamic field and method access.
 *
 * <p>Read {@link DynamicAccessDemo} first, then implement each method.
 * None of the methods declares a checked exception — wrap reflective checked
 * exceptions as described in each Javadoc.
 */
public class DynamicAccessTasks {

    /**
     * Read the value of the field called {@code fieldName} from {@code target},
     * whatever its visibility. The field may be declared on the object's class
     * <b>or on any of its super-classes</b> (the closest declaration wins).
     *
     * <p>Examples:
     * <pre>
     *   readField(new Account("Ann", 50), "owner")          → "Ann"      (private)
     *   readField(new SavingsAccount(), "rate")             → 5          (private, own class)
     *   readField(new SavingsAccount(), "balance")          → 100        (private, in super-class)
     *   readField(new Account(), "nope")                    → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getDeclaredField} only looks at one class — loop over {@code getSuperclass()}</li>
     *   <li>Private fields need {@code setAccessible(true)}</li>
     *   <li>A missing field is signalled by a checked exception: translate it</li>
     * </ul>
     *
     * @param target    the object to read from (never {@code null})
     * @param fieldName the simple name of the field
     * @return the field's current value (primitives are boxed)
     * @throws IllegalArgumentException if no class in the hierarchy declares such a field
     * @throws IllegalStateException    if the field exists but cannot be read
     */
    public Object readField(Object target, String fieldName) {
        throw new UnsupportedOperationException("TODO: implement readField");
    }

    /**
     * Write {@code value} into the field called {@code fieldName} of {@code target},
     * whatever its visibility. The lookup rules are the same as in
     * {@link #readField(Object, String)}.
     *
     * <p>Examples:
     * <pre>
     *   writeField(account, "balance", 500)      → account.getBalance() == 500
     *   writeField(savings, "rate", 9)           → savings.getRate() == 9
     *   writeField(savings, "balance", 1)        → savings.getBalance() == 1   (field of the super-class)
     *   writeField(account, "nope", 1)           → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Share the "find the field" logic with your {@code readField} solution</li>
     *   <li>{@code Field.set} accepts a boxed {@code Integer} for an {@code int} field</li>
     * </ul>
     *
     * @param target    the object to modify (never {@code null})
     * @param fieldName the simple name of the field
     * @param value     the new value
     * @throws IllegalArgumentException if no class in the hierarchy declares such a field
     * @throws IllegalStateException    if the field exists but cannot be written
     */
    public void writeField(Object target, String fieldName, Object value) {
        throw new UnsupportedOperationException("TODO: implement writeField");
    }

    /**
     * Invoke the <b>no-argument</b> method called {@code methodName} on {@code target}.
     * The method may have any visibility and may be declared on the object's class
     * or on a super-class.
     *
     * <p>If the invoked method throws a {@code RuntimeException}, that
     * <em>original</em> exception must reach the caller (not a wrapper).
     * Any other failure is reported as {@code IllegalStateException}
     * with the original exception as its cause.
     *
     * <p>Examples:
     * <pre>
     *   invokeNoArg(new Account("Ann", 50), "secret")          → "secret-Ann"   (private)
     *   invokeNoArg(new SavingsAccount(), "getOwner")          → "saver"        (declared in super-class)
     *   invokeNoArg(account, "unknownMethod")                  → IllegalArgumentException
     *   invokeNoArg(accountThatThrows, "explode")              → the original IllegalStateException("boom")
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getDeclaredMethod(name)} with no type arguments finds a no-arg method</li>
     *   <li>When {@code invoke} fails because the method threw, the real exception is
     *       the <em>cause</em> of the exception you catch</li>
     * </ul>
     *
     * @param target     the object to call the method on (never {@code null})
     * @param methodName the simple name of a no-argument method
     * @return the method's return value ({@code null} for {@code void})
     * @throws IllegalArgumentException if no no-arg method with that name exists in the hierarchy
     */
    public Object invokeNoArg(Object target, String methodName) {
        throw new UnsupportedOperationException("TODO: implement invokeNoArg");
    }

    /**
     * Create an instance of {@code type} using the constructor whose parameter
     * types are exactly {@code paramTypes}, even if that constructor is private.
     *
     * <p>If the constructor itself throws a {@code RuntimeException}, rethrow that
     * original exception. Every other failure becomes an {@code IllegalStateException}
     * (with the cause attached).
     *
     * <p>Examples:
     * <pre>
     *   newInstance(Account.class, new Class[]{String.class, int.class}, new Object[]{"Ann", 50})
     *       → Account with owner "Ann", balance 50
     *   newInstance(Account.class, new Class[]{String.class}, new Object[]{"Bob"})
     *       → Account with owner "Bob", balance 10      (private constructor)
     *   newInstance(Account.class, new Class[0], new Object[0])
     *       → Account with owner "nobody"
     *   newInstance(Account.class, new Class[]{double.class}, new Object[]{1.0})
     *       → IllegalArgumentException (no such constructor)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getDeclaredConstructor} sees private constructors; unlock it first</li>
     *   <li>Remember how the demo unwraps {@code InvocationTargetException}</li>
     * </ul>
     *
     * @param type       the class to instantiate
     * @param paramTypes the exact constructor parameter types
     * @param args       the constructor arguments
     * @param <T>        the class type
     * @return the new instance
     * @throws IllegalArgumentException if no constructor with those parameter types exists
     */
    public <T> T newInstance(Class<T> type, Class<?>[] paramTypes, Object[] args) {
        throw new UnsupportedOperationException("TODO: implement newInstance");
    }

    /**
     * Snapshot the state of an object as a map of {@code fieldName → value}.
     * Include every <b>non-static</b> field declared on the object's class
     * <b>and on all its super-classes</b>, whatever the visibility.
     * Compiler-generated (synthetic) fields are skipped.
     * If a sub-class and a super-class declare a field with the same name,
     * the sub-class value wins.
     *
     * <p>Examples:
     * <pre>
     *   toFieldMap(new Account("Ann", 50))
     *       → {owner=Ann, balance=50, currency=USD}          (static MAX_BALANCE is NOT included)
     *   toFieldMap(new SavingsAccount())
     *       → {owner=saver, balance=100, currency=USD, rate=5}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Walk up with {@code getSuperclass()} until you reach {@code Object}
     *       (or {@code null})</li>
     *   <li>{@code Modifier.isStatic} and {@code Field.isSynthetic} are your filters</li>
     *   <li>Use {@code get} on the field — remember to unlock private ones</li>
     * </ul>
     *
     * @param target the object to snapshot (never {@code null})
     * @return a map from field name to current value
     */
    public Map<String, Object> toFieldMap(Object target) {
        throw new UnsupportedOperationException("TODO: implement toFieldMap");
    }
}
