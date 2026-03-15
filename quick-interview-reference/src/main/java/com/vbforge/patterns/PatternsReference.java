package com.vbforge.patterns;

import java.util.*;
import java.util.function.Supplier;

/**
 * ═══════════════════════════════════════════════════════════════
 *  DESIGN PATTERNS INTERVIEW REFERENCE
 *  Topics: Singleton · Builder · Factory · Strategy ·
 *          Observer · Decorator · SOLID principles
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class PatternsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. SINGLETON — thread-safe patterns
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  COMMON INTERVIEW QUESTION: "How to make Singleton thread-safe?"
     *
     *  Option 1 — Enum Singleton (BEST — handles serialization + reflection attacks)
     *  Option 2 — Initialization-on-demand holder (lazy, thread-safe, no sync overhead)
     *  Option 3 — Double-checked locking with volatile (classic pattern)
     *  Option 4 — Eager initialization (simple if instance is always needed)
     */

    // ✅ Option 1: Enum Singleton — serialization-safe, reflection-safe
    enum EnumSingleton {
        INSTANCE;
        public void doSomething() { System.out.println("singleton"); }
    }

    // ✅ Option 2: Holder pattern — lazy, thread-safe, no synchronized overhead
    static class HolderSingleton {
        private HolderSingleton() {}
        private static class Holder {
            static final HolderSingleton INSTANCE = new HolderSingleton();
        }
        public static HolderSingleton getInstance() { return Holder.INSTANCE; }
        // Holder class loaded only when getInstance() is first called — JVM class loading is thread-safe
    }

    // ✅ Option 3: Double-checked locking — volatile is REQUIRED (Java 5+)
    static class DCLSingleton {
        private static volatile DCLSingleton instance;  // volatile: prevents instruction reordering
        private DCLSingleton() {}
        public static DCLSingleton getInstance() {
            if (instance == null) {                   // first check (no sync) — fast path
                synchronized (DCLSingleton.class) {
                    if (instance == null) {           // second check (with sync) — safe creation
                        instance = new DCLSingleton();
                    }
                }
            }
            return instance;
        }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. BUILDER — when constructors become unwieldy
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  USE BUILDER WHEN:
     *  - 4+ constructor parameters
     *  - Many optional parameters
     *  - Need immutable object after construction
     *  - Object creation requires validation across multiple fields
     */
    static final class HttpRequest {
        private final String url;
        private final String method;
        private final Map<String, String> headers;
        private final int timeoutMs;

        private HttpRequest(Builder b) {
            this.url       = Objects.requireNonNull(b.url, "url required");
            this.method    = b.method;
            this.headers   = Map.copyOf(b.headers);
            this.timeoutMs = b.timeoutMs;
        }

        static class Builder {
            private final String url;
            private String method  = "GET";
            private Map<String, String> headers = new HashMap<>();
            private int timeoutMs = 5000;

            Builder(String url) { this.url = url; }
            Builder method(String m)  { this.method = m; return this; }
            Builder header(String k, String v) { headers.put(k, v); return this; }
            Builder timeout(int ms)   { this.timeoutMs = ms; return this; }
            HttpRequest build()       { return new HttpRequest(this); }
        }
    }

    static void builderUsage() {
        HttpRequest req = new HttpRequest.Builder("https://api.example.com")
            .method("POST")
            .header("Content-Type", "application/json")
            .timeout(10_000)
            .build();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. STRATEGY — swap algorithms at runtime
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Modern Java: Strategy is often just a functional interface + lambda.
     *  No need for separate strategy classes in most cases.
     */
    interface SortStrategy { void sort(int[] arr); }

    static class Sorter {
        private SortStrategy strategy;
        Sorter(SortStrategy strategy) { this.strategy = strategy; }
        void setStrategy(SortStrategy s) { this.strategy = s; }
        void sort(int[] arr) { strategy.sort(arr); }
    }

    static void strategyUsage() {
        int[] arr = {5, 3, 1, 4, 2};
        Sorter sorter = new Sorter(Arrays::sort);                      // Java's built-in
        sorter.setStrategy(arr2 -> { /* bubble sort */ });             // swap at runtime
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. OBSERVER — event notification
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Modern Java: use java.util.function.Consumer as observer — no interface needed.
     *  Remember: always provide a way to DEREGISTER to avoid memory leaks.
     */
    static class EventBus<T> {
        private final List<java.util.function.Consumer<T>> listeners = new ArrayList<>();
        void subscribe(java.util.function.Consumer<T> listener) { listeners.add(listener); }
        void unsubscribe(java.util.function.Consumer<T> listener) { listeners.remove(listener); }
        void publish(T event) { listeners.forEach(l -> l.accept(event)); }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. SOLID PRINCIPLES — one example each
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  S — Single Responsibility: class has ONE reason to change
     *      ❌ UserService saves users AND sends emails
     *      ✅ UserService saves users; EmailService sends emails
     *
     *  O — Open/Closed: open for extension, closed for modification
     *      ❌ Add new payment type → modify switch in PaymentProcessor
     *      ✅ Add new PaymentHandler implementation, no existing code changes
     *
     *  L — Liskov Substitution: subtype must be substitutable for its supertype
     *      ❌ Square extends Rectangle, overrides setWidth to also setHeight
     *          → violates LSP: caller expecting Rectangle behavior gets broken Square
     *      ✅ Square and Rectangle are separate classes, or use interface Shape
     *
     *  I — Interface Segregation: don't force clients to depend on unused methods
     *      ❌ Fat interface: Animal { eat(); fly(); swim(); run(); }
     *          → Fish forced to implement fly()
     *      ✅ Flyable, Swimmable, Runnable separate interfaces
     *
     *  D — Dependency Inversion: depend on abstractions, not concretions
     *      ❌ UserService creates new MySQLRepository() directly — tightly coupled
     *      ✅ UserService receives UserRepository interface via constructor injection
     */

    // LSV violation example
    static class Rectangle {
        int width, height;
        void setWidth(int w)  { this.width = w; }
        void setHeight(int h) { this.height = h; }
        int area() { return width * height; }
    }

    // ❌ Violates LSP — setWidth changes height, breaking Rectangle contract
    static class Square extends Rectangle {
        @Override void setWidth(int w)  { this.width = w; this.height = w; }
        @Override void setHeight(int h) { this.width = h; this.height = h; }
    }

    // DIP example
    interface UserRepository { void save(Object user); }

    // ✅ UserService depends on abstraction, not concrete MySQL class
    static class UserService {
        private final UserRepository repo;
        UserService(UserRepository repo) { this.repo = repo; }  // injected
        void create(Object user) { repo.save(user); }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Singleton without volatile in DCL         → instruction reordering → broken instance
     *  ❌ Not providing unsubscribe in Observer     → memory leak
     *  ❌ Telescoping constructors (10 overloads)   → use Builder instead
     *  ❌ Overusing patterns                        → YAGNI — simpler code is better
     *  ❌ God class (does everything)               → violates SRP
     *  ❌ instanceof chains instead of polymorphism → violates OCP
     */

    public static void main(String[] args) {
        EnumSingleton.INSTANCE.doSomething();
        builderUsage();
        strategyUsage();

        EventBus<String> bus = new EventBus<>();
        bus.subscribe(e -> System.out.println("received: " + e));
        bus.publish("hello");
    }
}
