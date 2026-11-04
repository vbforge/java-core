package com.vbforge.reflection_api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic proxies — creating an implementation of an interface <em>at runtime</em>,
 * routing every call through one {@link InvocationHandler}.
 *
 * <p>This is the mechanism behind Spring AOP ({@code @Transactional}, {@code @Cacheable}),
 * Mockito mocks, Retrofit clients and JPA repositories. It combines everything else in
 * this module: class introspection, {@code Method.invoke}, and annotations on methods.
 */
public class DynamicProxyDemo {

    // ── Fixtures ────────────────────────────────────────────────────────

    /** Interface to be proxied. */
    public interface Calculator {
        int add(int a, int b);

        int divide(int a, int b);

        String name();
    }

    /** Real implementation. */
    public static class SimpleCalculator implements Calculator {
        @Override
        public int add(int a, int b) {
            return a + b;
        }

        @Override
        public int divide(int a, int b) {
            return a / b;
        }

        @Override
        public String name() {
            return "simple";
        }
    }

    /** Second interface used by the read-only-view task. */
    public interface Repository {
        String get(String key);

        void put(String key, String value);

        void remove(String key);
    }

    /** Map-backed {@link Repository}. */
    public static class MapRepository implements Repository {
        private final Map<String, String> data = new HashMap<String, String>();

        @Override
        public String get(String key) {
            return data.get(key);
        }

        @Override
        public void put(String key, String value) {
            data.put(key, value);
        }

        @Override
        public void remove(String key) {
            data.remove(key);
        }
    }

    /** Security annotation read by {@link AccessGuardHandler}. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RequiresRole {
        String value();
    }

    /** Service whose {@code delete} is protected by an annotation on the <em>interface</em> method. */
    public interface UserService {
        String findName(int id);

        @RequiresRole("ADMIN")
        void delete(int id);
    }

    /** Implementation — note that its {@code delete} carries no annotation of its own. */
    public static class InMemoryUserService implements UserService {
        private final List<Integer> deleted = new ArrayList<Integer>();

        @Override
        public String findName(int id) {
            return "user-" + id;
        }

        @Override
        public void delete(int id) {
            deleted.add(id);
        }

        public List<Integer> deletedIds() {
            return deleted;
        }
    }

    // ── Handlers ────────────────────────────────────────────────────────

    /** Records every call as {@code name(arg1, arg2)} and delegates — with correct unwrapping. */
    public static class RecordingHandler implements InvocationHandler {
        private final Object target;
        private final List<String> calls = new ArrayList<String>();

        public RecordingHandler(Object target) {
            this.target = target;
        }

        public List<String> calls() {
            return calls;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            calls.add(method.getName() + "(" + join(proxy, args) + ")");
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();                       // hand the REAL exception to the caller
            }
        }

        private static String join(Object proxy, Object[] args) {
            if (args == null) {                           // no-arg methods receive null, not an empty array
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                // printing the proxy itself would call proxy.toString() -> handler again
                sb.append(args[i] == proxy ? "<proxy>" : String.valueOf(args[i]));
            }
            return sb.toString();
        }
    }

    /** Deliberately naive: forgets to unwrap {@code InvocationTargetException}. */
    public static class NaiveHandler implements InvocationHandler {
        private final Object target;

        public NaiveHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(target, args);
        }
    }

    /** Annotation-driven security: reads {@code @RequiresRole} from the interface method. */
    public static class AccessGuardHandler implements InvocationHandler {
        private final Object target;
        private final String currentRole;

        public AccessGuardHandler(Object target, String currentRole) {
            this.target = target;
            this.currentRole = currentRole;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RequiresRole required = method.getAnnotation(RequiresRole.class);
            if (required != null && !required.value().equals(currentRole)) {
                throw new SecurityException(method.getName() + " requires role " + required.value());
            }
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    /** Typed factory so callers avoid casts. */
    @SuppressWarnings("unchecked")
    public static <T> T proxyOf(Class<T> iface, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] {iface}, handler);
    }

    // ── Demo entry point ────────────────────────────────────────────────

    public static void main(String[] args) {
        creationDemo();
        System.out.println("---");
        interceptionDemo();
        System.out.println("---");
        exceptionDemo();
        System.out.println("---");
        objectMethodsDemo();
        System.out.println("---");
        annotationGuardDemo();
    }

    // ── 1. Creating a proxy ─────────────────────────────────────────────

    /**
     * <b>{@code Proxy.newProxyInstance(loader, interfaces, handler)}.</b>
     *
     * <p>The JDK generates a class at runtime that implements the given interfaces; every
     * method on it simply calls {@code handler.invoke(proxy, method, args)}.
     *
     * <ul>
     *   <li>The proxy <b>is a</b> {@code Calculator} — and is <b>not</b> a {@code SimpleCalculator}</li>
     *   <li>{@code Proxy.isProxyClass} and {@code Proxy.getInvocationHandler} reveal the trick</li>
     *   <li>Only <em>interfaces</em> can be proxied — passing a class throws
     *       {@code IllegalArgumentException} (CGLIB / ByteBuddy are needed for classes)</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> one handler can add logging, caching, transactions or security
     * to <em>any</em> interface without writing a decorator per interface.
     *
     * <p><b>Complexity:</b> the proxy class is generated once per interface set and then cached;
     * each call adds one reflective {@code Method.invoke}.
     *
     * <p><b>Gotcha:</b> casting the proxy to the implementation class fails with
     * {@code ClassCastException} — always program against the interface.
     */
    static void creationDemo() {
        RecordingHandler handler = new RecordingHandler(new SimpleCalculator());
        Calculator proxy = proxyOf(Calculator.class, handler);

        System.out.println("proxy instanceof Calculator       : " + (proxy instanceof Calculator));
        System.out.println("proxy instanceof SimpleCalculator : " + (((Object) proxy) instanceof SimpleCalculator));
        System.out.println("Proxy.isProxyClass                : " + Proxy.isProxyClass(proxy.getClass()));
        System.out.println("getInvocationHandler is ours      : " + (Proxy.getInvocationHandler(proxy) == handler));

        try {
            Proxy.newProxyInstance(Calculator.class.getClassLoader(),
                    new Class<?>[] {SimpleCalculator.class}, handler);
        } catch (IllegalArgumentException e) {
            System.out.println("proxying a class -> IllegalArgumentException");
        }
    }

    // ── 2. Interception ─────────────────────────────────────────────────

    /**
     * <b>The handler sees every call and decides what happens.</b>
     *
     * <p>It receives the {@code Method} (from the <em>interface</em>), the arguments
     * (boxed; {@code null} when the method has no parameters) and returns the result
     * (boxed; {@code null} for {@code void}). Here it records the call and delegates to the
     * real object with {@code method.invoke(target, args)}.
     *
     * <p><b>Gotcha:</b> {@code args} is {@code null} — not an empty array — for no-arg methods.
     * Code that does {@code args.length} throws {@code NullPointerException} inside the proxy.
     */
    static void interceptionDemo() {
        RecordingHandler handler = new RecordingHandler(new SimpleCalculator());
        Calculator calc = proxyOf(Calculator.class, handler);

        System.out.println("add(2, 3) : " + calc.add(2, 3));
        System.out.println("name()    : " + calc.name());
        System.out.println("recorded  : " + handler.calls());
    }

    // ── 3. Exceptions ───────────────────────────────────────────────────

    /**
     * <b>Unwrap {@code InvocationTargetException} — or the caller gets the wrong exception.</b>
     *
     * <p>When the real method throws, {@code Method.invoke} wraps the exception in
     * {@code InvocationTargetException}. If the handler lets that escape, the proxy
     * cannot throw it (a checked exception not declared by {@code divide}) and converts it into
     * {@code UndeclaredThrowableException}. The correct handler throws
     * {@code e.getCause()}, so the caller sees a plain {@code ArithmeticException}.
     *
     * <p><b>Why it matters:</b> a proxy must be invisible — callers should be unable to tell
     * whether they talk to the real object.
     *
     * <p><b>Gotcha:</b> a checked exception thrown from a handler that the interface method
     * does <em>not</em> declare is always wrapped in {@code UndeclaredThrowableException}.
     */
    static void exceptionDemo() {
        Calculator good = proxyOf(Calculator.class, new RecordingHandler(new SimpleCalculator()));
        Calculator naive = proxyOf(Calculator.class, new NaiveHandler(new SimpleCalculator()));

        try {
            good.divide(1, 0);
        } catch (ArithmeticException e) {
            System.out.println("unwrapping handler -> " + e.getClass().getSimpleName());
        }
        try {
            naive.divide(1, 0);
        } catch (RuntimeException e) {
            System.out.println("naive handler      -> " + e.getClass().getSimpleName());
        }
    }

    // ── 4. Methods of Object ────────────────────────────────────────────

    /**
     * <b>{@code toString()}, {@code hashCode()} and {@code equals()} go through the handler too.</b>
     *
     * <p>Delegating them to the target looks natural — and produces a trap:
     * {@code proxy.equals(proxy)} becomes {@code target.equals(proxy)}, which is
     * {@code false} because the target is a different object than the proxy.
     * {@code toString()} and {@code hashCode()} are those of the <em>target</em>.
     *
     * <p><b>Why it matters:</b> proxies placed in a {@code HashSet} or compared in an
     * assertion behave in surprising ways unless the handler treats these three methods
     * explicitly.
     *
     * <p><b>Gotcha:</b> never call {@code proxy.toString()} <em>inside</em> the handler —
     * that re-enters the handler and ends in {@code StackOverflowError}.
     */
    static void objectMethodsDemo() {
        SimpleCalculator target = new SimpleCalculator();
        RecordingHandler handler = new RecordingHandler(target);
        Calculator proxy = proxyOf(Calculator.class, handler);

        System.out.println("proxy.toString() == target.toString() : "
                + proxy.toString().equals(target.toString()));
        System.out.println("proxy.hashCode() == target.hashCode() : "
                + (proxy.hashCode() == target.hashCode()));
        System.out.println("proxy.equals(proxy)                   : " + proxy.equals(proxy));
        System.out.println("recorded                              : " + handler.calls());
    }

    // ── 5. Annotation-driven proxy ──────────────────────────────────────

    /**
     * <b>Putting it together: annotation-driven security (a mini Spring AOP).</b>
     *
     * <p>The handler reads {@code @RequiresRole} from the {@code Method} it is given and
     * blocks the call when the current role does not match.
     *
     * <p><b>Why it matters:</b> the business class stays free of security code — the rule lives
     * in one annotation and one handler.
     *
     * <p><b>Gotcha:</b> the {@code Method} passed to the handler is the <em>interface</em>
     * method. The annotation sits there, <b>not</b> on {@code InMemoryUserService.delete}
     * — method annotations are never inherited from an interface. If you put
     * {@code @RequiresRole} only on the implementation, this handler would not see it.
     */
    static void annotationGuardDemo() {
        InMemoryUserService real = new InMemoryUserService();
        UserService asUser = proxyOf(UserService.class, new AccessGuardHandler(real, "USER"));
        UserService asAdmin = proxyOf(UserService.class, new AccessGuardHandler(real, "ADMIN"));

        System.out.println("findName as USER : " + asUser.findName(7));
        try {
            asUser.delete(7);
        } catch (SecurityException e) {
            System.out.println("delete as USER   : " + e.getMessage());
        }
        asAdmin.delete(7);
        System.out.println("delete as ADMIN  : deleted " + real.deletedIds());
    }
}
