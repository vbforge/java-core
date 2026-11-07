package com.vbforge.practice.map.practice_32_hashtable;

import java.util.Hashtable;
import java.util.Map;

/**
 * Hashtable basics — the legacy synchronized Map.
 *
 * Hashtable is a legacy class introduced in Java 1.0 that implements a synchronized
 * hash table. It was part of the original Java Collections, later retrofitted to
 * implement the Map interface.
 */
public class HashtableBasicsDemo {

    public static void main(String[] args) {
        introScene();
        basicOperationsScene();
        addingElementsScene();
        changingElementsScene();
        removingElementScene();
    }

    // Scene 1 — intro
    private static void introScene() {
        System.out.println("=== Scene 1: intro ===");
        Hashtable<String, String> capitals = new Hashtable<>();

        capitals.put("USA", "Washington");
        capitals.put("France", "Paris");
        capitals.put("Germany", "Berlin");

        System.out.println("Capital of France: " + capitals.get("France"));
    }

    // Scene 2 — basic operations (put/get/size/containsKey)
    private static void basicOperationsScene() {
        System.out.println("\n=== Scene 2: basic operations ===");
        Hashtable<String, Integer> ht = new Hashtable<>();

        ht.put("player1", 10);
        ht.put("player2", 30);
        ht.put("player3", 20);

        System.out.println("Size of map is: " + ht.size());
        System.out.println(ht);

        if (ht.containsKey("player1")) {
            Integer a = ht.get("player1");
            System.out.println("value for key"
                    + " \"player1\" is: " + a);
        }
    }

    // Scene 3 — adding elements
    private static void addingElementsScene() {
        System.out.println("\n=== Scene 3: adding elements ===");

        /*  1. Adding Elements: In order to add an element to the hashtable, we can use the put() method.
            However, the insertion order is not retained in the hashtable. Internally, for every element,
            a separate hash is generated and the elements are indexed based on this hash to make
            it more efficient.   */

        // No need to mention the Generic type twice
        Hashtable<Integer, String> ht1 = new Hashtable<>();

        // Initialization of a Hashtable using Generics
        Hashtable<Integer, String> ht2
                = new Hashtable<Integer, String>();

        // Inserting the Elements using put() method
        ht1.put(1, "Geeks");
        ht1.put(2, "For");
        ht1.put(3, "Geeks");

        ht2.put(1, "Geeks");
        ht2.put(2, "For");
        ht2.put(3, "Geeks");

        // Print mappings to the console
        System.out.println("Mappings of ht1 : " + ht1);
        System.out.println("Mappings of ht2 : " + ht2);
    }

    // Scene 4 — changing elements (re-put same key overwrites)
    private static void changingElementsScene() {
        System.out.println("\n=== Scene 4: changing elements ===");

        /*      2. Changing Elements: After adding the elements if we wish to change the element,
        it can be done by again adding the element with the put() method. Since the elements in
         the hashtable are indexed using the keys, the value of the key can be changed by simply
         inserting the updated value for the key for which we wish to change.*/

        // Initialization of a Hashtable
        Hashtable<Integer, String> ht = new Hashtable<Integer, String>();

        // Inserting the Elements using put method
        ht.put(1, "Player");
        ht.put(2, "--insert--");
        ht.put(3, "Year");

        // print initial map to the console
        System.out.println("Initial Map " + ht);

        // Update the value at key 2
        ht.put(2, "Of the");

        // print the updated map
        System.out.println("Updated Map " + ht);
    }

    // Scene 5 — removing elements
    private static void removingElementScene() {
        System.out.println("\n=== Scene 5: removing elements ===");

        /*      3. Removing Element: In order to remove an element from the Map, we can use
        the remove() method. This method takes the key value and removes the mapping for a
        key from this map if it is present in the map.*/

        // Initialization of a Hashtable
        Map<Integer, String> ht = new Hashtable<Integer, String>();

        // Inserting the Elements using put method
        ht.put(1, "Inter");
        ht.put(2, "Milan");
        ht.put(3, "Italy");
        ht.put(4, "Italy");

        // Initial Hashtable
        System.out.println("Initial map : " + ht);

        // Remove the map entry with key 4
        ht.remove(4);

        // Final Hashtable
        System.out.println("Updated map : " + ht);

        // containsKey after remove
        Hashtable<String, String> data = new Hashtable<>();
        data.put("user1", "John");
        data.put("user2", "Jane");
        data.remove("user1");
        System.out.println(data.containsKey("user1")); // false
    }
}
