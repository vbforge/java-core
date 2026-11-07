package com.vbforge.practice.map.practice_32_hashtable;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * All four Hashtable constructor variants, one scene each.
 */
public class HashtableConstructorsDemo {

    public static void main(String[] args) {
        defaultConstructorScene();
        capacityConstructorScene();
        capacityAndFillRatioScene();
        fromMapConstructorScene();
    }

    // Scene 1 — default constructor (cap=11, loadFactor=0.75)
    private static void defaultConstructorScene() {
        System.out.println("=== Scene 1: default constructor ===");

        /*  1. Hashtable(): This creates an empty hashtable with the default
        load factor of 0.75 and an initial capacity is 11.

            Hashtable<K, V> ht = new Hashtable<K, V>();*/

        Hashtable<Integer, String> ht1 = new Hashtable<>();
        Hashtable<Integer, String> ht2
                = new Hashtable<Integer, String>();

        ht1.put(1, "one");
        ht1.put(2, "two");
        ht1.put(3, "three");

        ht2.put(4, "four");
        ht2.put(5, "five");
        ht2.put(6, "six");

        System.out.println("Mappings of ht1 : " + ht1);
        System.out.println("Mappings of ht2 : " + ht2);
    }

    // Scene 2 — Hashtable(int initialCapacity)
    private static void capacityConstructorScene() {
        System.out.println("\n=== Scene 2: Hashtable(int initialCapacity) ===");

        /* 2. Hashtable(int initialCapacity): This creates a hash table that has an initial size
            specified by initialCapacity and the default load factor is 0.75.
            Hashtable<K, V> ht = new Hashtable<K, V>(int initialCapacity);*/

        Hashtable<Integer, String> ht1 = new Hashtable<>(4);
        Hashtable<Integer, String> ht2
                = new Hashtable<Integer, String>(2);

        ht1.put(1, "one");
        ht1.put(2, "two");
        ht1.put(3, "three");

        ht2.put(4, "four");
        ht2.put(5, "five");
        ht2.put(6, "six");

        System.out.println("Mappings of ht1 : " + ht1);
        System.out.println("Mappings of ht2 : " + ht2);
    }

    // Scene 3 — Hashtable(int size, float fillRatio)
    private static void capacityAndFillRatioScene() {
        System.out.println("\n=== Scene 3: Hashtable(int size, float fillRatio) ===");

        /*3. Hashtable(int size, float fillRatio): This version creates a hash table that
        has an initial size specified by size and fill ratio specified by fillRatio. fill ratio:
        Basically, it determines how full a hash table can be before it is resized upward and its
        Value lies between 0.0 to 1.0.
        Hashtable<K, V> ht = new Hashtable<K, V>(int size, float fillRatio);*/

        Hashtable<Integer, String> ht1
                = new Hashtable<>(4, 0.75f);

        Hashtable<Integer, String> ht2 =  new Hashtable<Integer, String>(3, 0.5f);

        ht1.put(1, "one");
        ht1.put(2, "two");
        ht1.put(3, "three");

        ht2.put(4, "four");
        ht2.put(5, "five");
        ht2.put(6, "six");

        System.out.println("Mappings of ht1 : " + ht1);
        System.out.println("Mappings of ht2 : " + ht2);
    }

    // Scene 4 — Hashtable(Map<? extends K, ? extends V> m)
    private static void fromMapConstructorScene() {
        System.out.println("\n=== Scene 4: Hashtable(Map m) ===");

        /* 4. Hashtable(Map<? extends K,? extends V> m): This creates a hash table that is
        initialized with the elements in m.
        Hashtable<K, V> ht = new Hashtable<K, V>(Map m);*/

        Map<Integer, String> hm = new HashMap<>();

        hm.put(1, "one");
        hm.put(2, "two");
        hm.put(3, "three");

        Hashtable<Integer, String> ht2
                = new Hashtable<Integer, String>(hm);

        System.out.println("Mappings of ht2 : " + ht2);
    }
}
