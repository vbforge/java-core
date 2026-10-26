package com.vbforge.serialization;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TransientAndVersionDemo — controlling WHAT is serialized and WHICH class versions are compatible.
 *
 * <p>Read top-to-bottom. Like {@link ObjectStreamsDemo}, everything runs on in-memory bytes.
 *
 * <p>Topics:
 * <ol>
 *   <li>transient — fields that are skipped, and static fields</li>
 *   <li>serialVersionUID — the class "version stamp" and what happens when it does not match</li>
 *   <li>Custom writeObject / readObject — taking control of the stream format</li>
 *   <li>readResolve — keeping singletons (and enums) unique after deserialization</li>
 * </ol>
 */
public class TransientAndVersionDemo {

    public static void main(String[] args) throws Exception {
        transientDemo();
        System.out.println("---");
        serialVersionUidDemo();
        System.out.println("---");
        customSerializationDemo();
        System.out.println("---");
        readResolveDemo();
    }

    // ── Model classes used by the demos (public so tests can reuse them) ──────

    /** Credentials-like class: some fields must never be written. */
    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String username;
        private transient String password;        // sensitive → never written
        private transient int loginAttempts;      // runtime state → not worth persisting
        private transient boolean authenticated;  // runtime state

        public User(String username, String password, int loginAttempts, boolean authenticated) {
            this.username = username;
            this.password = password;
            this.loginAttempts = loginAttempts;
            this.authenticated = authenticated;
        }

        public String getUsername()      { return username; }
        public String getPassword()      { return password; }
        public int getLoginAttempts()    { return loginAttempts; }
        public boolean isAuthenticated() { return authenticated; }
    }

    /** Has a STATIC field: static belongs to the class, not to any instance. */
    public static class Settings implements Serializable {
        private static final long serialVersionUID = 1L;

        public static String theme = "light";

        private final String owner;

        public Settings(String owner) { this.owner = owner; }

        public String getOwner() { return owner; }
    }

    /** Typical use of transient: a cache that can always be recomputed. */
    public static class Report implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<Integer> numbers;
        private transient Integer cachedSum;      // derived data → recompute instead of storing

        public Report(List<Integer> numbers) { this.numbers = new ArrayList<>(numbers); }

        public int sum() {
            if (cachedSum == null) {
                int total = 0;
                for (int n : numbers) {
                    total += n;
                }
                cachedSum = total;
            }
            return cachedSum;
        }

        public boolean isCached() { return cachedSum != null; }
    }

    /** Declares its version explicitly. */
    public static class Versioned implements Serializable {
        public static final long UID = 42L;
        private static final long serialVersionUID = UID;

        private final String data;

        public Versioned(String data) { this.data = data; }

        public String getData() { return data; }
    }

    /** Does NOT declare serialVersionUID — the JVM computes one from the class structure. */
    public static class Unversioned implements Serializable {
        private final String data;

        public Unversioned(String data) { this.data = data; }

        public String getData() { return data; }
    }

    /** The declared UID is NOT {@code final}, so the JVM ignores it and computes its own. */
    public static class NonFinalVersion implements Serializable {
        @SuppressWarnings("unused")
        private static long serialVersionUID = 42L;      // missing 'final'

        private final String data;

        public NonFinalVersion(String data) { this.data = data; }

        public String getData() { return data; }
    }

    /** The hook method is PUBLIC instead of private — serialization never calls it. */
    public static class NonPrivateHook implements Serializable {
        private static final long serialVersionUID = 1L;

        public static boolean hookCalled = false;

        private final String value;

        public NonPrivateHook(String value) { this.value = value; }

        public String getValue() { return value; }

        public void writeObject(ObjectOutputStream out) throws IOException {   // must be private!
            hookCalled = true;
            out.defaultWriteObject();
        }
    }

    /** A transient field that is still persisted — by hand, in a (weakly) obfuscated form. */
    public static class Account implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String owner;
        private transient String secret;

        public Account(String owner, String secret) {
            this.owner = owner;
            this.secret = secret;
        }

        public String getOwner()  { return owner; }
        public String getSecret() { return secret; }

        @SuppressWarnings("unused") // called reflectively by ObjectOutputStream
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();               // 1) writes all NON-transient fields (owner)
            out.writeObject(obfuscate(secret));     // 2) we decide how the transient field is stored
        }

        @SuppressWarnings("unused") // called reflectively by ObjectInputStream
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();                 // must read in the same order as written
            this.secret = obfuscate((String) in.readObject());
        }

        // Reversing a string is obfuscation, NOT encryption — it only keeps the plain value
        // out of the stream so the demo can prove the hook was used.
        private static String obfuscate(String s) {
            return s == null ? null : new StringBuilder(s).reverse().toString();
        }
    }

    /** Derived transient field that is rebuilt in readObject. */
    public static class Circle implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double radius;
        private transient double area;

        public Circle(double radius) {
            this.radius = radius;
            this.area = Math.PI * radius * radius;
        }

        public double getRadius() { return radius; }
        public double getArea()   { return area; }

        @SuppressWarnings("unused") // called reflectively by ObjectInputStream
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            this.area = Math.PI * radius * radius;   // constructor did not run, so recompute here
        }
    }

    /** Same as {@link Circle} but WITHOUT the readObject hook — the derived field is lost. */
    public static class PlainCircle implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double radius;
        private transient double area;

        public PlainCircle(double radius) {
            this.radius = radius;
            this.area = Math.PI * radius * radius;
        }

        public double getArea() { return area; }
    }

    /** Singleton that survives deserialization thanks to {@code readResolve}. */
    public static final class Registry implements Serializable {
        private static final long serialVersionUID = 1L;

        public static final Registry INSTANCE = new Registry();

        private Registry() { }

        @SuppressWarnings("unused") // called reflectively by ObjectInputStream
        private Object readResolve() {
            return INSTANCE;                         // replace the freshly built copy
        }
    }

    /** Singleton WITHOUT {@code readResolve} — deserialization creates a second instance. */
    public static final class BrokenRegistry implements Serializable {
        private static final long serialVersionUID = 1L;

        public static final BrokenRegistry INSTANCE = new BrokenRegistry();

        private BrokenRegistry() { }
    }

    /** Enums are serialized by constant NAME, so identity is always preserved. */
    public enum Level { LOW, HIGH }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Returns a copy of {@code stream} in which the first occurrence of {@code oldUid}
     * (8 big-endian bytes) is replaced by {@code newUid}.
     *
     * <p>The class descriptor inside the stream stores the {@code serialVersionUID} of the
     * class that wrote it. Rewriting those 8 bytes simulates "the stream was written by a
     * different version of the class" without needing two versions of the class on disk.
     *
     * @throws IllegalArgumentException if {@code oldUid} is not present in the stream
     */
    public static byte[] patchSerialVersionUID(byte[] stream, long oldUid, long newUid) {
        byte[] result = stream.clone();
        byte[] oldBytes = ByteBuffer.allocate(8).putLong(oldUid).array();
        byte[] newBytes = ByteBuffer.allocate(8).putLong(newUid).array();

        for (int i = 0; i <= result.length - 8; i++) {
            boolean match = true;
            for (int j = 0; j < 8 && match; j++) {
                match = result[i + j] == oldBytes[j];
            }
            if (match) {
                System.arraycopy(newBytes, 0, result, i, 8);
                return result;
            }
        }
        throw new IllegalArgumentException("serialVersionUID " + oldUid + " not found in stream");
    }

    // ── 1. transient ──────────────────────────────────────────────────────────

    /**
     * {@code transient} marks an instance field as "not part of the persistent state":
     * default serialization skips it, and after deserialization it holds the DEFAULT value
     * for its type ({@code null}, {@code 0}, {@code false}).
     *
     * <p>WHY this matters: three typical reasons to skip a field —
     * <ul>
     *   <li>it is sensitive (passwords, tokens)</li>
     *   <li>it is derived and can be recomputed (caches, sums)</li>
     *   <li>it cannot be serialized at all (threads, sockets, a non-serializable field type)</li>
     * </ul>
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>No constructor runs on deserialization, so a {@code transient} field does NOT get
     *       its constructor or initializer value back — it is {@code null}/{@code 0}/{@code false}
     *       (see {@code PlainCircle} in section 3, whose constructor computes {@code area})</li>
     *   <li>{@code static} fields are never serialized either — they belong to the class.
     *       After deserialization you see the CURRENT static value, not the one at write time</li>
     * </ul>
     */
    static void transientDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== transient ===");

        User user = new User("alice", "s3cret", 2, true);
        User copy = ObjectStreamsDemo.roundTrip(user);
        System.out.println("username (kept)             : " + copy.getUsername());       // alice
        System.out.println("password (transient)        : " + copy.getPassword());       // null
        System.out.println("loginAttempts (transient)   : " + copy.getLoginAttempts());  // 0
        System.out.println("authenticated (transient)   : " + copy.isAuthenticated());   // false

        // static fields are not part of the stream
        String previousTheme = Settings.theme;
        try {
            byte[] data = ObjectStreamsDemo.toBytes(new Settings("alice"));
            Settings.theme = "dark";                                                       // changed AFTER writing
            Settings restored = (Settings) ObjectStreamsDemo.fromBytes(data);
            System.out.println("static theme after read     : " + Settings.theme + " (owner " + restored.getOwner() + ")"); // dark
        } finally {
            Settings.theme = previousTheme;
        }

        // transient as a recomputable cache
        Report report = new Report(Arrays.asList(1, 2, 3, 4));
        report.sum();
        Report reportCopy = ObjectStreamsDemo.roundTrip(report);
        System.out.println("cache before / after read   : " + report.isCached() + " / " + reportCopy.isCached()); // true / false
        System.out.println("sum recomputed on demand    : " + reportCopy.sum());         // 10
    }

    // ── 2. serialVersionUID ───────────────────────────────────────────────────

    /**
     * {@code serialVersionUID} is a {@code long} "version stamp" stored in every serialized
     * class descriptor. When reading, the JVM compares the stamp in the stream with the stamp
     * of the class currently on the classpath; if they differ it throws
     * {@link InvalidClassException} instead of guessing.
     *
     * <p>WHY this matters: it is the guard between "the class changed" and "old bytes still
     * exist". Declare it explicitly ({@code private static final long serialVersionUID})
     * and YOU decide when versions become incompatible.
     *
     * <p>If you do NOT declare it, the JVM computes one from the class structure — you do
     * not control when that value changes.
     *
     * <p>Common gotcha: the field must be both {@code static} and {@code final}. Without
     * {@code final} it is silently ignored and the computed value is used instead.
     */
    static void serialVersionUidDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== serialVersionUID ===");

        long declared = ObjectStreamClass.lookup(Versioned.class).getSerialVersionUID();
        long computed = ObjectStreamClass.lookup(Unversioned.class).getSerialVersionUID();
        System.out.println("declared in source          : " + declared);                  // 42
        System.out.println("computed by the JVM         : " + computed);                  // some long
        System.out.println("computed value is stable    : "
                + (computed == ObjectStreamClass.lookup(Unversioned.class).getSerialVersionUID())); // true
        System.out.println("non-final UID (declared 42) : "
                + ObjectStreamClass.lookup(NonFinalVersion.class).getSerialVersionUID());     // NOT 42 — ignored

        byte[] data = ObjectStreamsDemo.toBytes(new Versioned("payload"));
        byte[] tampered = patchSerialVersionUID(data, Versioned.UID, Versioned.UID + 1);   // pretend: written by v43

        Versioned ok = (Versioned) ObjectStreamsDemo.fromBytes(data);
        System.out.println("matching UID                : read '" + ok.getData() + "'");
        try {
            ObjectStreamsDemo.fromBytes(tampered);
        } catch (InvalidClassException e) {
            System.out.println("mismatching UID             : InvalidClassException");
            System.out.println("  message                   : " + e.getMessage());
        }
    }

    // ── 3. Custom writeObject / readObject ────────────────────────────────────

    /**
     * A class can add two PRIVATE methods with exact signatures that serialization finds
     * by reflection:
     * <pre>
     *   private void writeObject(ObjectOutputStream out) throws IOException
     *   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
     * </pre>
     *
     * <p>WHY this matters: they let you (1) persist a transient field in a custom form,
     * and (2) rebuild derived state that the missing constructor call would otherwise leave
     * empty.
     *
     * <p>Rules:
     * <ul>
     *   <li>Call {@code defaultWriteObject()} / {@code defaultReadObject()} FIRST to handle
     *       all non-transient fields</li>
     *   <li>Whatever you write after that must be read back in the SAME order</li>
     * </ul>
     *
     * <p>Common gotcha: the methods must be {@code private} — a {@code public} version is
     * silently ignored (the demo shows the hook is never called).
     */
    static void customSerializationDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== writeObject / readObject ===");

        Account account = new Account("alice", "hunter2");
        byte[] data = ObjectStreamsDemo.toBytes(account);
        String raw = new String(data, StandardCharsets.ISO_8859_1);
        System.out.println("plain secret in stream      : " + raw.contains("hunter2"));   // false
        System.out.println("reversed secret in stream   : " + raw.contains("2retnuh"));   // true

        Account restored = (Account) ObjectStreamsDemo.fromBytes(data);
        System.out.println("owner / secret restored     : " + restored.getOwner() + " / " + restored.getSecret()); // alice / hunter2

        boolean before = NonPrivateHook.hookCalled;
        try {
            NonPrivateHook.hookCalled = false;
            ObjectStreamsDemo.toBytes(new NonPrivateHook("v"));
            System.out.println("public writeObject called   : " + NonPrivateHook.hookCalled);   // false — ignored
        } finally {
            NonPrivateHook.hookCalled = before;
        }

        Circle circle = ObjectStreamsDemo.roundTrip(new Circle(2.0));
        PlainCircle plain = ObjectStreamsDemo.roundTrip(new PlainCircle(2.0));
        System.out.println("area with readObject hook   : " + circle.getArea());          // 12.566...
        System.out.println("area without the hook       : " + plain.getArea());           // 0.0
    }

    // ── 4. readResolve ────────────────────────────────────────────────────────

    /**
     * Deserialization always creates a NEW object — which silently breaks the singleton
     * guarantee. A private {@code readResolve()} method lets the class replace the freshly
     * built object with one of its own choosing, typically the existing instance.
     *
     * <p>WHY this matters: {@code ==} comparisons and "exactly one instance" assumptions
     * stay valid after a round trip.
     *
     * <p>Good news: enums need no help. They are serialized by constant name and resolved
     * back to the existing constant, so identity is always preserved — which is why an
     * enum is the simplest serialization-safe singleton.
     */
    static void readResolveDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== readResolve ===");

        Registry registry = ObjectStreamsDemo.roundTrip(Registry.INSTANCE);
        BrokenRegistry broken = ObjectStreamsDemo.roundTrip(BrokenRegistry.INSTANCE);
        Level level = ObjectStreamsDemo.roundTrip(Level.HIGH);

        System.out.println("with readResolve  same instance : " + (registry == Registry.INSTANCE));       // true
        System.out.println("without it        same instance : " + (broken == BrokenRegistry.INSTANCE));  // false
        System.out.println("enum constant     same instance : " + (level == Level.HIGH));                 // true
    }
}
