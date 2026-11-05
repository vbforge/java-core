package com.vbforge.practice.practice_14_nested_classes_overview;

/**
 * The four kinds of nested classes side by side.
 *
 * Kind                  | static? | Where declared      | Access outer?
 * ----------------------|---------|---------------------|---------------
 * Static nested class   | yes     | class body          | static members only
 * Inner class           | no      | class body          | all outer members
 * Local class           | no      | method/block body   | effectively-final locals
 * Anonymous class       | no      | expression          | effectively-final locals
 *
 * When to use each:
 *   Static nested  - helper type logically grouped with outer, no outer reference needed
 *   Inner class    - tightly coupled to an outer instance, needs access to its state
 *   Local class    - when the class is only relevant inside one method
 *   Anonymous      - one-shot implementation of an interface / abstract class
 */
public class NestedClassesOverviewDemo {

    private String outerName = "Outer";
    private int    outerValue = 42;

    // 1. STATIC NESTED CLASS: no reference to an outer instance (helper / builder / key types)
    public static class Node<T> {
        T       data;
        Node<T> next;

        public Node(T data) {
            this.data = data;
        }

        @Override public String toString() { return String.valueOf(data); }
    }

    // 2. INNER CLASS: implicitly holds a reference to the enclosing instance
    public class EventListener {
        private String eventType;

        public EventListener(String eventType) {
            this.eventType = eventType;
        }

        public void onEvent() {
            System.out.println("Event[" + eventType + "] fired on: "
                + outerName + " (value=" + outerValue + ")");
        }
    }

    // 3. LOCAL CLASS: declared inside a method
    public void demonstrateLocalClass(String prefix) {
        class PrefixedPrinter {
            void print(String msg) {
                // 'prefix' must be effectively final (not reassigned after capture)
                System.out.println(prefix + " >> " + msg);
            }
        }

        PrefixedPrinter printer = new PrefixedPrinter();
        printer.print("Hello from local class");
        printer.print("Accessing prefix: " + prefix);
    }

    // 4. ANONYMOUS CLASS: inline implementation
    interface Greeter {
        String greet(String name);
    }

    public static void main(String[] args) {
        System.out.println("-- 1. Static nested class (Node<T>) --");
        Node<Integer> n1 = new Node<>(10);   // no outer instance needed
        Node<Integer> n2 = new Node<>(20);
        n1.next = n2;
        System.out.println("n1=" + n1 + " -> n2=" + n2);

        System.out.println("\n-- 2. Inner class (EventListener) --");
        NestedClassesOverviewDemo outer = new NestedClassesOverviewDemo();
        NestedClassesOverviewDemo.EventListener listener = outer.new EventListener("CLICK");   // through an outer instance
        listener.onEvent();

        System.out.println("\n-- 3. Local class (PrefixedPrinter) --");
        outer.demonstrateLocalClass("[INFO]");

        System.out.println("\n-- 4. Anonymous class (Greeter) --");
        Greeter formal = new Greeter() {
            @Override
            public String greet(String name) {
                return "Good day, " + name + ".";
            }
        };

        Greeter casual = new Greeter() {
            @Override
            public String greet(String name) {
                return "Hey, " + name + "!";
            }
        };

        System.out.println(formal.greet("Alice"));
        System.out.println(casual.greet("Bob"));
    }
}
