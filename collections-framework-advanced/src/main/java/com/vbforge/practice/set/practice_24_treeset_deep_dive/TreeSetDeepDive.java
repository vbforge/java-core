package com.vbforge.practice.set.practice_24_treeset_deep_dive;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * TreeSet deep dive — the full NavigableSet story plus iteration and removal.
 *
 * WHAT IT IS:
 *   A Set that keeps its elements SORTED (red-black tree).
 *   Internally backed by a TreeMap<E, PRESENT> ⇒ same navigation API.
 *   • Elements must be Comparable, or you must supply a Comparator.
 *   • null elements are NOT allowed with natural ordering (NPE on add).
 *   • NO duplicates — equality is defined by compareTo/compare == 0,
 *     NOT by equals()  (subtle but important!).
 *   • Complexity: add/remove/contains = O(log n); iteration is sorted.
 *   • NOT thread-safe.
 */
public class TreeSetDeepDive {

    public static void main(String[] args) {
        naturalOrderingScene();
        navigationHelpersScene();
        rangeViewsScene();
        descendingViewsScene();
        customComparatorScene();
        duplicateRuleScene();
        nullElementScene();
        topNScene();
        iterationStylesScene();
        removalPatternsScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — natural ordering
    // ─────────────────────────────────────────────────────────────
    private static void naturalOrderingScene() {
        System.out.println("=== Scene 1: natural ordering ===");
        NavigableSet<Integer> scores = new TreeSet<>();
        scores.add(50);
        scores.add(10);
        scores.add(30);
        scores.add(20);
        scores.add(40);
        scores.add(20);                                 // duplicate: ignored

        System.out.println("sorted          : " + scores);   // [10, 20, 30, 40, 50]
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — navigation helpers
    // ─────────────────────────────────────────────────────────────
    private static void navigationHelpersScene() {
        System.out.println("\n=== Scene 2: navigation helpers ===");
        NavigableSet<Integer> scores = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));

        System.out.println("first()          = " + scores.first());         // 10
        System.out.println("last()           = " + scores.last());          // 50
        System.out.println("floor(25)        = " + scores.floor(25));       // 20  (<=)
        System.out.println("lower(25)        = " + scores.lower(25));       // 20  (<)
        System.out.println("ceiling(25)      = " + scores.ceiling(25));     // 30  (>=)
        System.out.println("higher(25)       = " + scores.higher(25));      // 30  (>)
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 3 — range views (LIVE, backed by the set)
    // ─────────────────────────────────────────────────────────────
    private static void rangeViewsScene() {
        System.out.println("\n=== Scene 3: range views (live) ===");
        NavigableSet<Integer> scores = new TreeSet<>(java.util.List.of(10, 20, 30, 40, 50));

        System.out.println("headSet(30)              = " + scores.headSet(30, false));  // [10, 20]
        System.out.println("tailSet(30)              = " + scores.tailSet(30, true));   // [30, 40, 50]
        System.out.println("subSet(20, 40, true/false) = " + scores.subSet(20, true, 40, false)); // [20, 30]

        // Mutating a view mutates the set (careful!):
        scores.headSet(20, false).clear();              // removes 10
        System.out.println("after headSet<20 clear  : " + scores);           // [20, 30, 40, 50]
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — descending views + polls
    // ─────────────────────────────────────────────────────────────
    private static void descendingViewsScene() {
        System.out.println("\n=== Scene 4: descending views + polls ===");
        NavigableSet<Integer> scores = new TreeSet<>(java.util.List.of(20, 30, 40, 50));

        System.out.println("descendingSet()  = " + scores.descendingSet()); // [50, 40, 30, 20]
        System.out.println("pollFirst()      = " + scores.pollFirst());      // 20 (removes)
        System.out.println("pollLast()       = " + scores.pollLast());       // 50 (removes)
        System.out.println("after polls      = " + scores);                  // [30, 40]
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 5 — custom Comparator
    // ─────────────────────────────────────────────────────────────
    private static void customComparatorScene() {
        System.out.println("\n=== Scene 5: custom Comparator ===");
        NavigableSet<String> byLength =
                new TreeSet<>(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        byLength.add("aaaa");
        byLength.add("b");
        byLength.add("cc");
        byLength.add("abc");                             // same length as "cc"
        System.out.println("by length        : " + byLength);   // [b, cc, abc, aaaa]
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 6 — duplicate rule uses compare(), not equals()
    // ─────────────────────────────────────────────────────────────
    private static void duplicateRuleScene() {
        System.out.println("\n=== Scene 6: duplicate rule = compare(), not equals() ===");
        NavigableSet<String> caseInsensitive = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitive.add("Java");
        caseInsensitive.add("JAVA");                     // duplicate by comparator -> ignored
        caseInsensitive.add("python");
        System.out.println("case-insens set  : " + caseInsensitive);   // [Java, python]
        System.out.println("contains(java)   = " + caseInsensitive.contains("java"));  // true
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 7 — null element
    // ─────────────────────────────────────────────────────────────
    private static void nullElementScene() {
        System.out.println("\n=== Scene 7: null element ===");
        NavigableSet<Integer> scores = new TreeSet<>();
        try {
            scores.add(null);
        } catch (NullPointerException npe) {
            System.out.println("null add -> NPE  : " + npe.getClass().getSimpleName());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 8 — top-N / nearest values use case
    // ─────────────────────────────────────────────────────────────
    private static void topNScene() {
        System.out.println("\n=== Scene 8: top-N / latency ranking ===");
        NavigableSet<Long> latenciesMs = new TreeSet<>();
        for (long v : new long[]{12, 88, 5, 42, 61, 33, 120, 17, 55}) latenciesMs.add(v);
        System.out.println("all latencies    : " + latenciesMs);
        System.out.println("p50-ish (median) : " + latenciesMs.ceiling(50L));   // 55
        System.out.println("outliers > 100   : " + latenciesMs.tailSet(100L, false));
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 9 — iteration styles (folded from TreeSetHowToIterate)
    // ─────────────────────────────────────────────────────────────
    private static void iterationStylesScene() {
        System.out.println("\n=== Scene 9: iteration styles ===");

        TreeSet<String> fruits = new TreeSet<>();
        fruits.add("Banana");
        fruits.add("Apple");
        fruits.add("Cherry");
        fruits.add("Date");

        System.out.println("TreeSet contents (sorted): " + fruits);

        System.out.println("--- for-each ---");
        for (String fruit : fruits) System.out.println(fruit);

        System.out.println("--- Iterator ---");
        Iterator<String> iterator = fruits.iterator();
        while (iterator.hasNext()) System.out.println(iterator.next());

        System.out.println("--- forEach lambda ---");
        fruits.forEach(fruit -> System.out.println(fruit));

        System.out.println("--- method reference ---");
        fruits.forEach(System.out::println);

        System.out.println("--- Stream API ---");
        fruits.stream().forEach(System.out::println);

        System.out.println("--- descendingIterator ---");
        Iterator<String> descendingIterator = fruits.descendingIterator();
        while (descendingIterator.hasNext()) {
            System.out.println(descendingIterator.next());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 10 — removal patterns (folded from TreeSetHowToRemoveElements)
    // ─────────────────────────────────────────────────────────────
    private static void removalPatternsScene() {
        System.out.println("\n=== Scene 10: removal patterns ===");

        TreeSet<String> countries = new TreeSet<>();
        countries.add("Germany");
        countries.add("France");
        countries.add("Spain");
        countries.add("Italy");
        countries.add("Finland");

        System.out.println("Initial TreeSet (sorted): " + countries);

        // 1. Remove a specific element
        countries.remove("Spain");
        System.out.println("1. After remove(\"Spain\"): " + countries);

        // 2. Remove first and last elements (specific to TreeSet)
        countries.pollFirst();  // removes the lowest element
        countries.pollLast();   // removes the highest element
        System.out.println("2. After pollFirst() and pollLast(): " + countries);

        // Re-populate for next examples
        countries.clear();
        countries.add("Norway");
        countries.add("Sweden");
        countries.add("Denmark");
        countries.add("Finland");
        countries.add("Iceland");

        // 3. Safe removal while iterating using iterator
        Iterator<String> iterator = countries.iterator();
        while (iterator.hasNext()) {
            String country = iterator.next();
            if (country.startsWith("S")) {
                iterator.remove(); // safe removal
            }
        }
        System.out.println("3. After safely removing elements starting with 'S': " + countries);

        // 4. Remove using removeIf (Java 8+)
        countries.removeIf(country -> country.endsWith("land"));
        System.out.println("4. After removeIf(endsWith 'land'): " + countries);

        // 5. Clear the entire set
        countries.clear();
        System.out.println("5. After clear(): " + countries);
        System.out.println("Is empty? " + countries.isEmpty());
    }
}
