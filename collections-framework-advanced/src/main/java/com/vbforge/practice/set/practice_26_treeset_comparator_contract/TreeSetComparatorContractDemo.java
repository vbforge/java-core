package com.vbforge.practice.set.practice_26_treeset_comparator_contract;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * TreeSet ordering contract — four increasingly-broken scenarios:
 *
 *   Scene 1: homogeneous TreeSet<String> / TreeSet<Integer> — works (natural order).
 *    *   Scene 3: non-Comparable domain, no Comparator — ClassCastException on first add.
 *   Scene 4: "comparator" that always returns 1 — every add is "greater than"
 *            everything ⇒ every element goes to the right child ⇒ a
 *            degenerate linked list, and `contains` / duplicate-detection is broken.
 *   Scene 5: proper comparator by price — TreeSet works as intended.
 *
 * The lesson: TreeSet does NOT use equals() — it uses the comparator (or
 * natural compareTo). A bad comparator breaks both deduplication and lookup,
 * even though the underlying elements have perfectly good equals() methods.
 */
public class TreeSetComparatorContractDemo {

    public static void main(String[] args) {
        homogeneousScene();
        nonComparableNoComparatorScene();
        brokenComparatorScene();
        correctComparatorScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — homogeneous TreeSet works with natural ordering
    // (was MyApp01 — homogeneous part)
    // ─────────────────────────────────────────────────────────────
    private static void homogeneousScene() {
        System.out.println("=== Scene 1: homogeneous natural-order TreeSet ===");

        TreeSet<String> strings = new TreeSet<>();
        strings.add("w");
        strings.add("x");
        strings.add("y");
        strings.add("a");
        strings.add("b");
        strings.add("c");
        System.out.println(strings);
        strings.add("b");   // duplicate — ignored
        System.out.println(strings);

        TreeSet<Integer> ints = new TreeSet<>();
        ints.add(8);
        ints.add(5);
        ints.add(1);
        ints.add(3);
        System.out.println(ints);
        ints.add(5);        // duplicate — ignored
        System.out.println(ints);
    }


    // ─────────────────────────────────────────────────────────────
    // Scene 3 — non-Comparable + no Comparator → CCE
    // (was MyApp02 — setup)
    // ─────────────────────────────────────────────────────────────
    private static void nonComparableNoComparatorScene() {
        System.out.println("\n=== Scene 3: non-Comparable domain, no Comparator → CCE ===");

        Phone lg = new Phone();
        lg.price = 1;

        TreeSet<Phone> set = new TreeSet<>();
        try {
            set.add(lg);
            System.out.println("(unreachable)");
        } catch (ClassCastException cce) {
            System.out.println("ClassCastException — Phone does not implement Comparable");
            System.out.println("message: " + cce.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 4 — comparator that always returns 1 → broken ordering
    // (was MyApp02 — "just return 1" trap)
    // ─────────────────────────────────────────────────────────────
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void brokenComparatorScene() {
        System.out.println("\n=== Scene 4: broken comparator (always 1) ===");

        Phone lg = new Phone();
        lg.price = 1;
        Phone htc = new Phone();
        htc.price = 2;
        Phone samsung = new Phone();
        samsung.price = 3;

        TreeSet set = new TreeSet(new BadPhoneComparator());
        set.add(lg);
        set.add(htc);
        set.add(samsung);

        System.out.println(set);
        // No dedupe, no sensible ordering — every add is "greater than" every
        // existing element, so the tree degenerates into a linked list.
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 5 — correct comparator by price
    // (was MyApp03)
    // ─────────────────────────────────────────────────────────────
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void correctComparatorScene() {
        System.out.println("\n=== Scene 5: correct comparator (by price) ===");

        Phone a = new Phone(); a.price = 500;
        Phone b = new Phone(); b.price = 2000;
        Phone c = new Phone(); c.price = 1000;

        TreeSet set = new TreeSet(new PricePhoneComparator());
        set.add(a);
        set.add(b);
        set.add(c);
        System.out.println(set);

        Phone d = new Phone(); d.price = 500; // same price as 'a' → duplicate by comparator
        set.add(d);
        System.out.println(set);
    }

    // ─────────────────────────────────────────────────────────────
    // Support types — Phone (with no Comparable) + two comparators
    // ─────────────────────────────────────────────────────────────

    /**
     * Plain Phone — no Comparable. TreeSet<String> works because String is
     * Comparable; TreeSet<Phone> needs a Comparator.
     */
    static class Phone {
        public int price;

        @Override
        public String toString() {
            return "Phone{" + "price=" + price + '}';
        }
    }

    /**
     * Anti-pattern: always returns 1.
     *   any negative → obj1<obj2
     *   0            → obj1==obj2
     *   any positive → obj1>obj2
     *
     * Returning 1 on every comparison means TreeSet never thinks two elements
     * are equal and never builds a balanced tree — the comparator contract
     * (antisymmetry) is broken.
     */
    @SuppressWarnings("rawtypes")
    static class BadPhoneComparator implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            return 1;
        }
    }

    /**
     * Correct comparator — by price ascending.
     */
    @SuppressWarnings("rawtypes")
    static class PricePhoneComparator implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            if (obj1.getClass() != obj2.getClass()) {
                throw new RuntimeException("obj1.class!=obj2.class");
            }
            if (obj1.getClass() == Phone.class) {
                Phone p1 = (Phone) obj1;
                Phone p2 = (Phone) obj2;
                return p1.price - p2.price;
            }
            return 1;
        }
    }
}
