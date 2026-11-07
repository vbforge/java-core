package com.vbforge.practice.set.practice_21_linkedhashset_basics;

import java.util.Iterator;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;
import com.vbforge.practice.common.practice_2_hotel_sample_data.Room;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * LinkedHashSet basics — insertion-order + O(1) membership + safe removal +
 * iteration idioms + HashSet vs LinkedHashSet side-by-side on a Room domain.
 *
 *   Set:            unique, no index access, no order guarantee.
 *   LinkedHashSet:  overrides the ordering point — iteration follows insertion.
 */
public class LinkedHashSetBasicsDemo {

    public static void main(String[] args) {
        insertionOrderScene();
        hashSetVsLinkedHashSetScene();
        basicOperationsScene();
        iterationStylesScene();
        removalPatternsScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — insertion-order guarantee
    // ─────────────────────────────────────────────────────────────
    private static void insertionOrderScene() {
        System.out.println("=== Scene 1: insertion-order guarantee ===");

        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add("D");
        set.add("t");
        set.add("D");   // duplicate — ignored
        set.add("h");
        for (Object item : set) {
            System.out.println(item);
        }
        //set.get(0);   // no index access
        System.out.println(set.getClass());   // class java.util.LinkedHashSet
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — HashSet vs LinkedHashSet on a Room domain
    // (source: DemoSet)
    // ─────────────────────────────────────────────────────────────
    private static void hashSetVsLinkedHashSetScene() {
        System.out.println("\n=== Scene 2: HashSet vs LinkedHashSet (Room) ===");

        Room manchester = new Room("Manchester", "Presentation", 100, 850.0);
        Room london = new Room("London", "Guest", 10, 250.0);
        Room londonDuplicate = new Room("London", "Guest", 10, 250.0);
        Room oxford = new Room("Oxford", "Community", 5, 250.0);

//        Set<Room> roomSet = new HashSet<>(); //no **order**
        Set<Room> roomSet = new LinkedHashSet<>(); // **ordering** is available for LinkedHashSet()
        roomSet.add(manchester);
        roomSet.add(london);
        roomSet.add(londonDuplicate);
        roomSet.add(oxford);

        //1) not duplicates as expected
        //2) no order by HashSet()
        roomSet.stream()
                .map(Room::getCity)
                .forEach(System.out::println);
        /* HashSet:
        London
        Manchester
        Oxford*/

        /* LinkedHashSet:
        Manchester
        London
        Oxford*/
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — basic operations (from BasicMethodsLinkedHashSet)
    // ─────────────────────────────────────────────────────────────
    private static void basicOperationsScene() {
        System.out.println("\n=== Scene 3: basic operations ===");

        LinkedHashSet<String> fruits = new LinkedHashSet<>();

        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Cherry");
        fruits.add("Banana"); // duplicate, will not be added

        System.out.println("Fruits (insertion order preserved): " + fruits);
        System.out.println("Size: " + fruits.size());
        System.out.println("Contains 'Banana'? " + fruits.contains("Banana"));
        System.out.println("Contains 'Orange'? " + fruits.contains("Orange"));

        fruits.remove("Apple");
        System.out.println("After removing 'Apple': " + fruits);

        // Iteration using for-each
        System.out.println("Iterating using for-each:");
        for (String fruit : fruits) {
            System.out.println(fruit);
        }

        // Iteration using iterator (safe removal)
        System.out.println("Iterating using iterator (safe removal):");
        Iterator<String> iterator = fruits.iterator();
        while (iterator.hasNext()) {
            String fruit = iterator.next();
            if (fruit.startsWith("B")) {
                iterator.remove(); // safe to remove during iteration
            }
        }
        System.out.println("After removing items starting with 'B': " + fruits);

        fruits.clear();
        System.out.println("After clear, is empty? " + fruits.isEmpty());
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — iteration styles (from HowToIterateLinkedHashSet)
    // ─────────────────────────────────────────────────────────────
    private static void iterationStylesScene() {
        System.out.println("\n=== Scene 4: iteration styles ===");

        LinkedHashSet<String> cities = new LinkedHashSet<>();
        cities.add("London");
        cities.add("Paris");
        cities.add("Rome");
        cities.add("Berlin");

        System.out.println("--- for-each ---");
        for (String city : cities) {
            System.out.println(city);
        }

        System.out.println("--- Iterator ---");
        Iterator<String> iterator = cities.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("--- forEach lambda ---");
        cities.forEach(city -> System.out.println(city));

        System.out.println("--- method reference ---");
        cities.forEach(System.out::println);

        System.out.println("--- toArray() + indexed for ---");
        Object[] cityArray = cities.toArray();
        for (int i = 0; i < cityArray.length; i++) {
            System.out.println(cityArray[i]);
        }

        System.out.println("--- Stream API ---");
        cities.stream().forEach(System.out::println);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 5 — removal patterns (from HowToRemoveElementsLinkedHashSet)
    // ─────────────────────────────────────────────────────────────
    private static void removalPatternsScene() {
        System.out.println("\n=== Scene 5: removal patterns ===");

        LinkedHashSet<String> animals = new LinkedHashSet<>();
        animals.add("Dog");
        animals.add("Cat");
        animals.add("Bird");
        animals.add("Fish");
        animals.add("Cat"); // duplicate, ignored

        System.out.println("Initial Set: " + animals);

        animals.remove("Fish");
        System.out.println("After remove(\"Fish\"): " + animals);

        Iterator<String> iterator = animals.iterator();
        while (iterator.hasNext()) {
            String animal = iterator.next();
            if (animal.equals("Bird")) {
                iterator.remove(); // safe
            }
        }
        System.out.println("After safely removing 'Bird' using iterator: " + animals);

        animals.removeIf(animal -> animal.startsWith("D"));
        System.out.println("After removeIf (startsWith 'D'): " + animals);

        animals.clear();
        System.out.println("After clear(): " + animals);
        System.out.println("Is empty? " + animals.isEmpty());
    }
}
