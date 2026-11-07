package com.vbforge.practice.map.practice_32_hashtable;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
 * Iterating a Hashtable — two idioms:
 *   Scene 1: modern Map.Entry via entrySet()
 *   Scene 2: legacy Enumeration via keys()
 */
public class HashtableIterationDemo {

    public static void main(String[] args) {
        entrySetIterationScene();
        enumerationIterationScene();
    }

    // Scene 1 — entrySet() iteration (modern)
    private static void entrySetIterationScene() {
        System.out.println("=== Scene 1: entrySet() iteration ===");

        /*  4. Traversal of a Hashtable: To iterate the table, we can make use of an advanced for loop.
            Below is the example of iterating a hashtable*/

        Hashtable<String, Integer> table = new Hashtable<>();
        table.put("A", 10);
        table.put("B", 20);
        table.put("C", 30);

        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }

        // Alternative population, same pattern
        System.out.println();
        Hashtable<String, Integer> ht = new Hashtable<>();
        ht.put("Player1", 10);
        ht.put("Player2", 30);
        ht.put("Player3", 20);

        for (Map.Entry<String, Integer> e : ht.entrySet())
            System.out.println(e.getKey() + " " + e.getValue());
    }

    // Scene 2 — Enumeration (legacy)
    private static void enumerationIterationScene() {
        System.out.println("\n=== Scene 2: Enumeration (legacy) ===");
        Hashtable<String, Integer> hashtable = new Hashtable<>();

        hashtable.put("A", 1);
        hashtable.put("B", 2);
        hashtable.put("C", 3);

        int valueA = hashtable.get("A");
        System.out.println("Value of A: " + valueA);

        hashtable.remove("B");

        // Enumerating the elements of the hashtable
        Enumeration<String> keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            System.out.println("Key: " + key + ", Value: " + hashtable.get(key));
        }
    }
}
