package com.vbforge.reflection_api;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Dynamic field and method access — reading, writing and calling members
 * that are only known by <em>name</em> at runtime.
 *
 * <p>Fixtures ({@link Account}, {@link SavingsAccount}, {@link Settings}) are
 * nested so the demo and all tests share them.
 */
public class DynamicAccessDemo {

    // ── Fixtures ────────────────────────────────────────────────────────

    /** Bank account with private state, a compile-time constant and overloads. */
    public static class Account {
        private static final int MAX_BALANCE = 1_000_000;

        // A "constant variable": final + initialised with a literal. javac inlines it.
        private final String currency = "USD";

        private String owner;
        private int balance;

        public Account() {
            this("nobody", 0);
        }

        public Account(String owner, int balance) {
            this.owner = owner;
            this.balance = balance;
        }

        private Account(String owner) {
            this(owner, 10);
        }

        public String getOwner() {
            return owner;
        }

        public int getBalance() {
            return balance;
        }

        public String getCurrency() {
            return currency;
        }

        public void deposit(int amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive: " + amount);
            }
            balance += amount;
        }

        public String format(int n) {
            return "int:" + n;
        }

        public String format(String s) {
            return "str:" + s;
        }

        public static int doubleIt(int x) {
            return x * 2;
        }

        public static int maxBalance() {
            return MAX_BALANCE;
        }

        private String secret() {
            return "secret-" + owner;
        }
    }

    /** Sub-class adding its own private field — used to show hierarchy walking. */
    public static class SavingsAccount extends Account {
        private int rate = 5;

        public SavingsAccount() {
            super("saver", 100);
        }

        public int getRate() {
            return rate;
        }
    }

    /** Holder of a mutable static field. Always restore {@code mode} after changing it. */
    public static class Settings {
        public static String mode = "dev";
    }

    // ── Demo entry point ────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        fieldsDemo();
        System.out.println("---");
        finalFieldsDemo();
        System.out.println("---");
        methodsDemo();
        System.out.println("---");
        exceptionsDemo();
        System.out.println("---");
        constructorsDemo();
        System.out.println("---");
        arraysDemo();
        System.out.println("---");
        encapsulationDemo();
    }

    // ── 1. Reading and writing fields ───────────────────────────────────

    /**
     * <b>Reading and writing fields by name.</b>
     *
     * <ul>
     *   <li>{@code getField(name)} — public fields only (inherited included)</li>
     *   <li>{@code getDeclaredField(name)} — any visibility, but this class only</li>
     *   <li>{@code field.setAccessible(true)} — switches off the access check so
     *       private members can be used</li>
     *   <li>{@code field.get(target)} / {@code field.set(target, value)} — for a
     *       <em>static</em> field the target is ignored; pass {@code null}</li>
     *   <li>{@code getInt}/{@code setInt}/… avoid boxing for primitive fields</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> this is what a JSON mapper, an ORM or a
     * dependency-injection container does to populate objects it did not write.
     *
     * <p><b>Complexity:</b> the lookup ({@code getDeclaredField}) is O(f) in the number of
     * fields; {@code get}/{@code set} on an already-found {@code Field} are cheap.
     * Look up once, reuse the {@code Field}.
     *
     * <p><b>Gotcha:</b> without {@code setAccessible(true)} touching a private member
     * from another class throws {@code IllegalAccessException}. The demo shows this
     * with a JDK class whose fields we are definitely not allowed to touch.
     */
    static void fieldsDemo() throws Exception {
        Account account = new Account("Ann", 50);

        try {
            Account.class.getField("owner");            // private -> invisible to getField
        } catch (NoSuchFieldException e) {
            System.out.println("getField(\"owner\") on a private field -> NoSuchFieldException");
        }

        Field owner = Account.class.getDeclaredField("owner");
        owner.setAccessible(true);
        System.out.println("owner (before) : " + owner.get(account));
        owner.set(account, "Bob");
        System.out.println("owner (after)  : " + account.getOwner());

        Field balance = Account.class.getDeclaredField("balance");
        balance.setAccessible(true);
        balance.setInt(account, 999);                   // primitive accessor, no boxing
        System.out.println("balance        : " + balance.getInt(account));

        Field mode = Settings.class.getField("mode");   // public static -> target ignored
        try {
            mode.set(null, "prod");
            System.out.println("Settings.mode  : " + Settings.mode);
        } finally {
            mode.set(null, "dev");                      // restore shared static state
        }

        Field stringValue = String.class.getDeclaredField("value");
        try {
            stringValue.get("abc");                     // no setAccessible -> access check fails
        } catch (IllegalAccessException e) {
            System.out.println("private JDK field without setAccessible -> IllegalAccessException");
        }
    }

    // ── 2. Final fields ─────────────────────────────────────────────────

    /**
     * <b>{@code final} fields: reflection can change them, the compiler may not notice.</b>
     *
     * <ul>
     *   <li>A non-static {@code final} instance field <em>can</em> be written after
     *       {@code setAccessible(true)}</li>
     *   <li>A {@code static final} field <b>cannot</b> — {@code set} throws
     *       {@code IllegalAccessException} even after {@code setAccessible(true)}</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> a surprising number of "it works on my machine" bugs
     * come from code that bypasses {@code final}.
     *
     * <p><b>Gotcha — constant inlining:</b> {@code private final String currency = "USD"}
     * is a <em>constant variable</em>, so {@code javac} copies {@code "USD"} into every
     * place that reads it. After a reflective write the field holds {@code "EUR"}, yet
     * {@code getCurrency()} still returns {@code "USD"}. Never use reflection to
     * "change a constant" — the result is two diverging truths.
     */
    static void finalFieldsDemo() throws Exception {
        Account account = new Account("Ann", 50);

        Field currency = Account.class.getDeclaredField("currency");
        currency.setAccessible(true);
        currency.set(account, "EUR");
        System.out.println("field value (reflection) : " + currency.get(account));
        System.out.println("getCurrency() (inlined)  : " + account.getCurrency());

        Field max = Account.class.getDeclaredField("MAX_BALANCE");
        max.setAccessible(true);
        try {
            max.set(null, 5);
        } catch (IllegalAccessException e) {
            System.out.println("static final write -> IllegalAccessException");
        }
        System.out.println("maxBalance() unchanged   : " + Account.maxBalance());
    }

    // ── 3. Invoking methods ─────────────────────────────────────────────

    /**
     * <b>Calling methods by name with {@code Method.invoke}.</b>
     *
     * <ul>
     *   <li>{@code getMethod(name, paramTypes...)} — public, inherited included</li>
     *   <li>{@code getDeclaredMethod(name, paramTypes...)} — any visibility, this class only</li>
     *   <li>{@code method.invoke(target, args...)} — {@code target} is {@code null} for
     *       {@code static} methods; the result is always an {@code Object} (primitives are boxed)</li>
     * </ul>
     *
     * <p>Overloads are told apart <em>only</em> by the parameter-type list, so
     * {@code getMethod("format", int.class)} and {@code getMethod("format", String.class)}
     * are two different methods.
     *
     * <p><b>Why it matters:</b> test runners, command dispatchers and RPC layers invoke
     * methods named in a file or a request.
     *
     * <p><b>Complexity:</b> each {@code getMethod} call is a search (and returns a
     * <em>copy</em> of the {@code Method}), so cache the {@code Method} you need.
     * The copies are {@code equals} but not {@code ==}.
     *
     * <p><b>Gotcha:</b> for {@code deposit(int)} you must ask for {@code int.class};
     * {@code Integer.class} gives {@code NoSuchMethodException} — reflection does not
     * apply boxing when <em>looking up</em> a method (it does when <em>passing</em> arguments).
     */
    static void methodsDemo() throws Exception {
        Account account = new Account("Ann", 50);

        Method deposit = Account.class.getMethod("deposit", int.class);
        deposit.invoke(account, 25);                    // 25 is auto-boxed to Integer, then unboxed
        System.out.println("balance after deposit(25) : " + account.getBalance());

        Method formatInt = Account.class.getMethod("format", int.class);
        Method formatStr = Account.class.getMethod("format", String.class);
        System.out.println("format(int)    : " + formatInt.invoke(account, 7));
        System.out.println("format(String) : " + formatStr.invoke(account, "x"));

        Method doubleIt = Account.class.getMethod("doubleIt", int.class);
        System.out.println("static doubleIt(21) : " + doubleIt.invoke(null, 21));

        Method secret = Account.class.getDeclaredMethod("secret");
        secret.setAccessible(true);
        System.out.println("private secret() : " + secret.invoke(account));

        Method again = Account.class.getMethod("deposit", int.class);
        System.out.println("same Method object? " + (deposit == again) + ", equal? " + deposit.equals(again));

        try {
            Account.class.getMethod("deposit", Integer.class);
        } catch (NoSuchMethodException e) {
            System.out.println("getMethod(\"deposit\", Integer.class) -> NoSuchMethodException");
        }
    }

    // ── 4. Exceptions ───────────────────────────────────────────────────

    /**
     * <b>Three different failures, three different exceptions.</b>
     *
     * <ul>
     *   <li>Wrong arguments (count or types) → {@code IllegalArgumentException} thrown by {@code invoke}</li>
     *   <li>The invoked method itself throws → {@code InvocationTargetException}
     *       <em>wrapping</em> the real exception; read it with {@code getCause()}</li>
     *   <li>{@code null} target for an instance method → {@code NullPointerException}</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> a caller that catches {@code InvocationTargetException}
     * and rethrows it as-is hides the business exception from everyone above it.
     * Always unwrap {@code getCause()}.
     *
     * <p><b>Gotcha:</b> {@code catch (Exception e)} around {@code invoke} makes all three
     * look the same — you lose the information about <em>who</em> was at fault.
     */
    static void exceptionsDemo() throws Exception {
        Account account = new Account("Ann", 50);
        Method deposit = Account.class.getMethod("deposit", int.class);

        try {
            deposit.invoke(account, -5);
        } catch (InvocationTargetException e) {
            System.out.println("method threw        : " + e.getCause());
        }

        try {
            deposit.invoke(account, "text");
        } catch (IllegalArgumentException e) {
            System.out.println("wrong argument type : IllegalArgumentException");
        }

        try {
            deposit.invoke(account);
        } catch (IllegalArgumentException e) {
            System.out.println("wrong arg count     : IllegalArgumentException");
        }

        try {
            deposit.invoke(null, 5);
        } catch (NullPointerException e) {
            System.out.println("null target         : NullPointerException");
        }
    }

    // ── 5. Constructors ─────────────────────────────────────────────────

    /**
     * <b>Creating objects without {@code new}.</b>
     *
     * <ul>
     *   <li>{@code getConstructor(types...).newInstance(args...)} — public constructor</li>
     *   <li>{@code getDeclaredConstructor(types...)} + {@code setAccessible(true)} — private one</li>
     *   <li>{@code Class.newInstance()} is deprecated since Java 9 — it propagates
     *       checked exceptions it cannot declare. Always go through a {@code Constructor}.</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> this is the core of every DI container and of
     * deserialisers that build an object from a type name.
     *
     * <p><b>Gotcha:</b> like methods, constructors are looked up by exact parameter types
     * ({@code int.class}, not {@code Integer.class}), and a constructor that throws
     * is reported as {@code InvocationTargetException}.
     */
    static void constructorsDemo() throws Exception {
        Constructor<Account> publicCtor = Account.class.getConstructor(String.class, int.class);
        Account ann = publicCtor.newInstance("Ann", 50);
        System.out.println("public ctor  : " + ann.getOwner() + " / " + ann.getBalance());

        try {
            Account.class.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            System.out.println("getConstructor(String) on a private ctor -> NoSuchMethodException");
        }

        Constructor<Account> privateCtor = Account.class.getDeclaredConstructor(String.class);
        privateCtor.setAccessible(true);
        Account bob = privateCtor.newInstance("Bob");
        System.out.println("private ctor : " + bob.getOwner() + " / " + bob.getBalance());

        Account fallback = Account.class.getDeclaredConstructor().newInstance();
        System.out.println("no-arg ctor  : " + fallback.getOwner());
    }

    // ── 6. Reflective arrays ────────────────────────────────────────────

    /**
     * <b>{@code java.lang.reflect.Array} — arrays whose type is only known at runtime.</b>
     *
     * <p>{@code Array.newInstance(componentType, length)} creates a real
     * {@code int[]} / {@code String[]} (not an {@code Object[]} in disguise);
     * {@code Array.get/set/getLength} (plus primitive variants) read and write it.
     *
     * <p><b>Why it matters:</b> generic code such as {@code List.toArray(T[])} or a
     * deserialiser needs to build an array of a type it received as a {@code Class<?>}.
     *
     * <p><b>Gotcha:</b> {@code Array.newInstance(int.class, 3)} returns an
     * {@code Object} that <em>is</em> an {@code int[]} — cast it; an {@code Object[]}
     * cast would fail because primitive arrays are not object arrays.
     */
    static void arraysDemo() {
        Object ints = Array.newInstance(int.class, 3);
        Array.setInt(ints, 0, 7);
        Array.setInt(ints, 2, 9);
        int[] typed = (int[]) ints;
        System.out.println("int array     : " + typed[0] + "," + typed[1] + "," + typed[2]);
        System.out.println("length        : " + Array.getLength(ints));

        Object strings = Array.newInstance(String.class, 2);
        Array.set(strings, 0, "a");
        System.out.println("String[] ?    : " + (strings instanceof String[]));
        System.out.println("int[] is Object[]? " + (ints instanceof Object[]));
    }

    // ── 7. Strong encapsulation (Java 9+) ───────────────────────────────

    /**
     * <b>Reflection stops at module boundaries.</b>
     *
     * <p>Since Java 9 the JDK is split into modules, and since Java 16/17 strong
     * encapsulation is the default: {@code setAccessible(true)} on a <em>private</em>
     * member of a JDK class such as {@code java.lang.String} throws
     * {@code java.lang.reflect.InaccessibleObjectException} (a {@code RuntimeException}).
     * On Java 8 the same call succeeds. Your own classes on the classpath are
     * never affected.
     *
     * <p><b>Why it matters:</b> libraries that used to poke into JDK internals broke
     * when teams moved to Java 17. The fix is to use a public API — or, as a last
     * resort, the {@code --add-opens} JVM flag.
     *
     * <p><b>Gotcha:</b> the exception is raised by {@code setAccessible}, <em>not</em> by
     * {@code getDeclaredField} — you can look the field up, you just cannot unlock it.
     * (This repository builds on Java 17+, so this is what you will observe.)
     */
    static void encapsulationDemo() throws NoSuchFieldException {
        Field value = String.class.getDeclaredField("value");   // lookup is allowed
        System.out.println("found String.value : " + value.getName());
        try {
            value.setAccessible(true);
            System.out.println("setAccessible succeeded (Java 8 behaviour)");
        } catch (RuntimeException e) {
            System.out.println("setAccessible failed : " + e.getClass().getSimpleName());
        }
    }
}
