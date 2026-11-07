package com.vbforge.practice.reference.practice_1_big_o_complexity_reference;

/**
 * Big-O quick reference for the Java Collections Framework.
 *
 *   Structure        | get      | add      | remove   | contains
 *   -----------------|----------|----------|----------|----------
 *   ArrayList        | O(1)     | O(1)*    | O(n)     | O(n)
 *   LinkedList       | O(n)     | O(1)**   | O(1)**   | O(n)
 *   HashSet/HashMap  | O(1)†    | O(1)†    | O(1)†    | O(1)†
 *   TreeSet/TreeMap  | O(log n) | O(log n) | O(log n) | O(log n)
 *   ArrayDeque       | O(1)     | O(1)*    | O(1)     | O(n)
 *
 *   *  amortized (occasional array resize)
 *   ** at known node position
 *   †  amortized; worst case O(n) on hash collision storm
 *
 * Key decision rules:
 *   - Random access by index?        -> ArrayList
 *   - Frequent head/tail insert?     -> ArrayDeque (not LinkedList)
 *   - Uniqueness + fast lookup?      -> HashSet
 *   - Sorted order + fast lookup?    -> TreeSet
 *   - Key→Value with fast access?    -> HashMap
 *   - Key→Value with sorted keys?    -> TreeMap
 *   - Insertion-order Map?           -> LinkedHashMap
 *
 * This class is documentation-first — {@code main} prints a runnable
 * cheat-sheet so the table can be consulted without opening the IDE doc view.
 *
 * For behavioural demonstrations of these complexities, see:
 *   - {@code practice_2_arraylist_vs_linkedlist}
 *   - {@code practice_3_map_internals}
 *   - {@code practice_4_deque_internals}
 *   - {@code practice_5_enum_collections}
 *   - {@code practice_6_custom_data_structures}
 */
public class ComplexityReferenceDemo {

    public static void main(String[] args) {
        System.out.println("=== Big-O Quick Reference (Java Collections) ===\n");
        System.out.printf("%-18s | %-8s | %-9s | %-9s | %-8s%n",
                "Structure", "get", "add", "remove", "contains");
        System.out.println("-".repeat(62));
        printRow("ArrayList",       "O(1)",     "O(1)*",    "O(n)",     "O(n)");
        printRow("LinkedList",      "O(n)",     "O(1)**",   "O(1)**",   "O(n)");
        printRow("HashSet/HashMap", "O(1)†",    "O(1)†",    "O(1)†",    "O(1)†");
        printRow("TreeSet/TreeMap", "O(log n)", "O(log n)", "O(log n)", "O(log n)");
        printRow("ArrayDeque",      "O(1)",     "O(1)*",    "O(1)",     "O(n)");
        System.out.println();
        System.out.println("  *  amortized (occasional array resize)");
        System.out.println("  ** at known node position");
        System.out.println("  †  amortized; worst case O(n) on hash collision storm");
        System.out.println();

        System.out.println("=== Decision rules ===");
        System.out.println("  - Random access by index?        -> ArrayList");
        System.out.println("  - Frequent head/tail insert?     -> ArrayDeque (not LinkedList)");
        System.out.println("  - Uniqueness + fast lookup?      -> HashSet");
        System.out.println("  - Sorted order + fast lookup?    -> TreeSet");
        System.out.println("  - Key->Value with fast access?   -> HashMap");
        System.out.println("  - Key->Value with sorted keys?   -> TreeMap");
        System.out.println("  - Insertion-order Map?           -> LinkedHashMap");
    }

    private static void printRow(String name, String get, String add, String remove, String contains) {
        System.out.printf("%-18s | %-8s | %-9s | %-9s | %-8s%n", name, get, add, remove, contains);
    }
}
