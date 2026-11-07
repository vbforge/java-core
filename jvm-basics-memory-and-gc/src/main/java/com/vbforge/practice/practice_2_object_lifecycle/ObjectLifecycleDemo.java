package com.vbforge.practice.practice_2_object_lifecycle;

/**
 * Object lifecycle in Java:
 *   1. Creation    — `new` allocates on the heap, constructor runs
 *   2. In-use      — references exist
 *   3. Unreachable — no references (GC-eligible)
 *   4. Collected   — GC reclaims memory (finalize is deprecated)
 *
 * The equals/hashCode contract is critical for collections, but the
 * canonical treatment is in `equals-hashcode-and-object-contract`.
 */
public class ObjectLifecycleDemo {

    static final class Product {
        private final String sku;
        private final String name;
        private final double price;

        Product(String sku, String name, double price) {
            this.sku = sku;
            this.name = name;
            this.price = price;
        }

        @Override public String toString() {
            return "Product{sku='" + sku + "', name='" + name + "', price=" + price + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("--- creation ---");
        Product p1 = new Product("SKU-001", "Keyboard", 89.99);
        Product p2 = new Product("SKU-002", "Mouse",    35.50);
        System.out.println(p1);
        System.out.println(p2);

        System.out.println("\n--- references keep objects alive ---");
        Product p1Alias = p1;
        System.out.println("p1 == p1Alias: " + (p1 == p1Alias));

        System.out.println("\n--- nulling a reference makes it GC-eligible ---");
        Product temp = new Product("SKU-999", "Temp", 0.0);
        System.out.println("temp alive: " + temp);
        temp = null;
        System.out.println("temp set to null → the object becomes unreachable");
        System.out.println("(the actual collection happens on the JVM's schedule)");
    }
}