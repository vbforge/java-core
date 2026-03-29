package com.vbforge.arrays_collections_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CollectionsDemoTest — verifies every concept shown in {@link CollectionsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the Java Collections API.
 */
@DisplayName("CollectionsDemo — API contracts")
class CollectionsDemoTest {

    // ── 1. Arrays ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Arrays")
    class ArraysTest {

        @Test
        @DisplayName("length is a field, not a method")
        void lengthField() {
            int[] arr = {1, 2, 3, 4, 5};
            assertThat(arr.length).isEqualTo(5);
        }

        @Test
        @DisplayName("default values: 0, false, null")
        void defaultValues() {
            int[]     ints   = new int[3];
            boolean[] bools  = new boolean[3];
            String[]  strs   = new String[3];

            assertThat(ints[0]).isZero();
            assertThat(bools[0]).isFalse();
            assertThat(strs[0]).isNull();
        }

        @Test
        @DisplayName("Arrays.sort sorts in-place (ascending)")
        void sort() {
            int[] arr = {5, 3, 1, 4, 2};
            Arrays.sort(arr);
            assertThat(arr).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("Arrays.binarySearch returns correct index on sorted array")
        void binarySearch() {
            int[] sorted = {1, 2, 3, 4, 5};
            assertThat(Arrays.binarySearch(sorted, 3)).isEqualTo(2);
        }

        @Test
        @DisplayName("Arrays.copyOf truncates or pads with zeros")
        void copyOf() {
            int[] src = {1, 2, 3, 4, 5};
            assertThat(Arrays.copyOf(src, 3)).containsExactly(1, 2, 3);
            assertThat(Arrays.copyOf(src, 7)).containsExactly(1, 2, 3, 4, 5, 0, 0);
        }

        @Test
        @DisplayName("Arrays.copyOfRange copies a [from, to) slice")
        void copyOfRange() {
            int[] src = {10, 20, 30, 40, 50};
            assertThat(Arrays.copyOfRange(src, 1, 4)).containsExactly(20, 30, 40);
        }

        @Test
        @DisplayName("Arrays.fill replaces every element")
        void fill() {
            int[] arr = new int[4];
            Arrays.fill(arr, 7);
            assertThat(arr).containsOnly(7);
        }

        @Test
        @DisplayName("Arrays.equals is element-wise; == is reference comparison")
        void equalsVsRef() {
            int[] a = {1, 2, 3};
            int[] b = {1, 2, 3};
            assertThat(a == b).isFalse();
            assertThat(Arrays.equals(a, b)).isTrue();
        }

        @Test
        @DisplayName("2D array element access and deepToString")
        void twoDimensional() {
            int[][] m = {{1, 2}, {3, 4}};
            assertThat(m[1][0]).isEqualTo(3);
            assertThat(Arrays.deepToString(m)).isEqualTo("[[1, 2], [3, 4]]");
        }

        @Test
        @DisplayName("Arrays.asList returns a fixed-size backed list")
        void asListFixedSize() {
            String[] arr = {"a", "b", "c"};
            List<String> list = Arrays.asList(arr);
            assertThat(list).containsExactly("a", "b", "c");
            assertThatThrownBy(() -> list.add("d"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("new ArrayList(Arrays.asList(...)) is mutable")
        void mutableCopyFromArray() {
            List<String> mutable = new ArrayList<>(Arrays.asList("a", "b"));
            mutable.add("c");
            assertThat(mutable).containsExactly("a", "b", "c");
        }
    }

    // ── 2. ArrayList ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. ArrayList")
    class ArrayListTest {

        @Test
        @DisplayName("add() appends; add(index) inserts and shifts")
        void addAndInsert() {
            ArrayList<String> list = new ArrayList<>();
            list.add("A");
            list.add("C");
            list.add(1, "B");  // insert between A and C
            assertThat(list).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("get() and set() are O(1) by index")
        void getAndSet() {
            ArrayList<String> list = new ArrayList<>(List.of("x", "y", "z"));
            assertThat(list.get(1)).isEqualTo("y");
            list.set(1, "Y");
            assertThat(list.get(1)).isEqualTo("Y");
        }

        @Test
        @DisplayName("remove(int) removes by index; remove(Object) removes by value")
        void removeByIndexAndValue() {
            ArrayList<String> list = new ArrayList<>(List.of("A", "B", "C"));
            list.remove(0);           // removes "A"
            assertThat(list).containsExactly("B", "C");
            list.remove("C");         // removes by value
            assertThat(list).containsExactly("B");
        }

        @Test
        @DisplayName("contains() and indexOf() / lastIndexOf()")
        void searchMethods() {
            ArrayList<String> list = new ArrayList<>(List.of("a", "b", "a", "c"));
            assertThat(list.contains("b")).isTrue();
            assertThat(list.indexOf("a")).isEqualTo(0);
            assertThat(list.lastIndexOf("a")).isEqualTo(2);
        }

        @Test
        @DisplayName("subList() is a live view — mutations propagate")
        void subListIsView() {
            ArrayList<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
            List<Integer> sub = list.subList(1, 4);  // [2,3,4]
            sub.set(0, 99);
            assertThat(list.get(1)).isEqualTo(99);   // mutation reflected in original
        }

        @Test
        @DisplayName("sort with natural Comparator (ascending)")
        void sortNatural() {
            ArrayList<Integer> list = new ArrayList<>(List.of(5, 3, 1, 4, 2));
            Collections.sort(list);
            assertThat(list).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("sort with custom Comparator (descending)")
        void sortCustom() {
            ArrayList<Integer> list = new ArrayList<>(List.of(1, 3, 2));
            list.sort((a, b) -> b - a);
            assertThat(list).containsExactly(3, 2, 1);
        }

        @Test
        @DisplayName("clear() empties the list")
        void clear() {
            ArrayList<String> list = new ArrayList<>(List.of("a", "b"));
            list.clear();
            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("toArray() converts to Object array")
        void toArray() {
            ArrayList<String> list = new ArrayList<>(List.of("x", "y"));
            String[] arr = list.toArray(new String[0]);
            assertThat(arr).containsExactly("x", "y");
        }
    }

    // ── 3. LinkedList ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. LinkedList")
    class LinkedListTest {

        @Test
        @DisplayName("addFirst / addLast work at O(1)")
        void addFirstLast() {
            LinkedList<String> ll = new LinkedList<>();
            ll.addLast("B");
            ll.addFirst("A");
            ll.addLast("C");
            assertThat(ll).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("peekFirst / peekLast do NOT remove elements")
        void peekDoesNotRemove() {
            LinkedList<String> ll = new LinkedList<>(List.of("A", "B", "C"));
            assertThat(ll.peekFirst()).isEqualTo("A");
            assertThat(ll.peekLast()).isEqualTo("C");
            assertThat(ll).hasSize(3);  // unchanged
        }

        @Test
        @DisplayName("pollFirst / pollLast remove and return head/tail")
        void pollRemoves() {
            LinkedList<String> ll = new LinkedList<>(List.of("A", "B", "C"));
            assertThat(ll.pollFirst()).isEqualTo("A");
            assertThat(ll.pollLast()).isEqualTo("C");
            assertThat(ll).containsExactly("B");
        }

        @Test
        @DisplayName("push/pop — LIFO stack behaviour")
        void stackBehaviour() {
            LinkedList<String> stack = new LinkedList<>();
            stack.push("first");
            stack.push("second");
            stack.push("third");
            assertThat(stack.pop()).isEqualTo("third");   // LIFO
            assertThat(stack.pop()).isEqualTo("second");
        }

        @Test
        @DisplayName("offer/poll — FIFO queue behaviour")
        void queueBehaviour() {
            LinkedList<String> queue = new LinkedList<>();
            queue.offer("first");
            queue.offer("second");
            queue.offer("third");
            assertThat(queue.poll()).isEqualTo("first");  // FIFO
            assertThat(queue.poll()).isEqualTo("second");
        }
    }

    // ── 4. HashSet ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. HashSet")
    class HashSetTest {

        @Test
        @DisplayName("add() returns false for duplicates; size reflects unique count")
        void noDuplicates() {
            HashSet<String> set = new HashSet<>();
            assertThat(set.add("Alice")).isTrue();
            assertThat(set.add("Alice")).isFalse();
            assertThat(set).hasSize(1);
        }

        @Test
        @DisplayName("contains() is O(1) average")
        void contains() {
            Set<Integer> set = new HashSet<>(List.of(1, 2, 3));
            assertThat(set.contains(2)).isTrue();
            assertThat(set.contains(9)).isFalse();
        }

        @Test
        @DisplayName("remove() returns false if element not present")
        void removeMissing() {
            HashSet<String> set = new HashSet<>(Set.of("a", "b"));
            assertThat(set.remove("c")).isFalse();
        }

        @Test
        @DisplayName("union via addAll()")
        void union() {
            Set<Integer> a = new HashSet<>(List.of(1, 2, 3));
            Set<Integer> b = new HashSet<>(List.of(3, 4, 5));
            Set<Integer> union = new HashSet<>(a);
            union.addAll(b);
            assertThat(union).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("intersection via retainAll()")
        void intersection() {
            Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
            Set<Integer> b = new HashSet<>(List.of(3, 4, 5, 6));
            a.retainAll(b);
            assertThat(a).containsExactlyInAnyOrder(3, 4);
        }

        @Test
        @DisplayName("difference via removeAll()")
        void difference() {
            Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
            Set<Integer> b = new HashSet<>(List.of(3, 4, 5));
            a.removeAll(b);
            assertThat(a).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        @DisplayName("containsAll checks for subset")
        void subset() {
            Set<Integer> a = new HashSet<>(List.of(1, 2, 3, 4));
            assertThat(a.containsAll(Set.of(2, 3))).isTrue();
            assertThat(a.containsAll(Set.of(2, 9))).isFalse();
        }
    }

    // ── 5. HashMap ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. HashMap")
    class HashMapTest {

        @Test
        @DisplayName("put() returns previous value (null if new key)")
        void putReturnsPrevious() {
            HashMap<String, Integer> map = new HashMap<>();
            assertThat(map.put("a", 1)).isNull();
            assertThat(map.put("a", 2)).isEqualTo(1);
        }

        @Test
        @DisplayName("get() returns null for absent key")
        void getMissingKey() {
            HashMap<String, Integer> map = new HashMap<>();
            assertThat(map.get("missing")).isNull();
        }

        @Test
        @DisplayName("getOrDefault() returns fallback for absent key")
        void getOrDefault() {
            HashMap<String, Integer> map = new HashMap<>(Map.of("a", 1));
            assertThat(map.getOrDefault("a", 99)).isEqualTo(1);
            assertThat(map.getOrDefault("b", 99)).isEqualTo(99);
        }

        @Test
        @DisplayName("putIfAbsent() does not overwrite existing keys")
        void putIfAbsent() {
            HashMap<String, Integer> map = new HashMap<>(Map.of("a", 1));
            map.putIfAbsent("a", 999);
            map.putIfAbsent("b", 2);
            assertThat(map.get("a")).isEqualTo(1);
            assertThat(map.get("b")).isEqualTo(2);
        }

        @Test
        @DisplayName("remove(key, value) only removes when value matches")
        void removeKeyValue() {
            HashMap<String, Integer> map = new HashMap<>(Map.of("a", 1));
            assertThat(map.remove("a", 999)).isFalse(); // wrong value
            assertThat(map.remove("a", 1)).isTrue();
            assertThat(map.containsKey("a")).isFalse();
        }

        @Test
        @DisplayName("merge() accumulates counts cleanly")
        void merge() {
            HashMap<String, Integer> freq = new HashMap<>();
            List.of("a", "b", "a", "c", "a").forEach(w -> freq.merge(w, 1, Integer::sum));
            assertThat(freq).containsEntry("a", 3)
                            .containsEntry("b", 1)
                            .containsEntry("c", 1);
        }

        @Test
        @DisplayName("keySet(), values(), entrySet() return views of the map")
        void views() {
            HashMap<String, Integer> map = new HashMap<>(Map.of("x", 10, "y", 20));
            assertThat(map.keySet()).containsExactlyInAnyOrder("x", "y");
            assertThat(map.values()).containsExactlyInAnyOrder(10, 20);
            assertThat(map.entrySet()).hasSize(2);
        }
    }

    // ── 6. Iteration ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Iteration patterns")
    class IterationTest {

        @Test
        @DisplayName("for-each iterates all elements in order")
        void forEach() {
            List<String> result = new ArrayList<>();
            for (String s : List.of("a", "b", "c")) result.add(s);
            assertThat(result).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("Iterator.remove() is safe during iteration")
        void iteratorRemove() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                if (it.next() % 2 == 0) it.remove();  // remove even numbers
            }
            assertThat(list).containsExactly(1, 3, 5);
        }

        @Test
        @DisplayName("for-each remove throws ConcurrentModificationException")
        void forEachRemoveThrows() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
            assertThatThrownBy(() -> {
                for (Integer i : list) {
                    list.remove(i);   // structural modification during for-each
                }
            }).isInstanceOf(java.util.ConcurrentModificationException.class);
        }

        @Test
        @DisplayName("entrySet loop visits all key-value pairs")
        void entrySet() {
            Map<String, Integer> map = Map.of("a", 1, "b", 2);
            int sum = 0;
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                sum += e.getValue();
            }
            assertThat(sum).isEqualTo(3);
        }
    }

    // ── 7. Collections utility ────────────────────────────────────────────────

    @Nested
    @DisplayName("7. Collections utility class")
    class CollectionsUtilTest {

        @Test
        @DisplayName("sort and reverse")
        void sortAndReverse() {
            List<Integer> list = new ArrayList<>(List.of(3, 1, 4, 1, 5));
            Collections.sort(list);
            assertThat(list).containsExactly(1, 1, 3, 4, 5);
            Collections.reverse(list);
            assertThat(list).containsExactly(5, 4, 3, 1, 1);
        }

        @Test
        @DisplayName("min() and max()")
        void minMax() {
            List<Integer> list = List.of(3, 1, 4, 1, 5, 9);
            assertThat(Collections.min(list)).isEqualTo(1);
            assertThat(Collections.max(list)).isEqualTo(9);
        }

        @Test
        @DisplayName("frequency() counts occurrences")
        void frequency() {
            List<String> list = List.of("a", "b", "a", "c", "a");
            assertThat(Collections.frequency(list, "a")).isEqualTo(3);
        }

        @Test
        @DisplayName("nCopies() creates immutable list of n copies")
        void nCopies() {
            List<String> copies = Collections.nCopies(3, "x");
            assertThat(copies).containsExactly("x", "x", "x");
        }

        @Test
        @DisplayName("fill() replaces all elements with a value")
        void fill() {
            List<String> list = new ArrayList<>(List.of("a", "b", "c"));
            Collections.fill(list, "z");
            assertThat(list).containsOnly("z");
        }

        @Test
        @DisplayName("unmodifiableList() blocks structural modifications")
        void unmodifiable() {
            List<String> base = new ArrayList<>(List.of("a", "b"));
            List<String> ro   = Collections.unmodifiableList(base);
            assertThatThrownBy(() -> ro.add("c"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("List.of() is truly immutable — no nulls, no add/remove")
        void listOf() {
            List<String> immutable = List.of("a", "b", "c");
            assertThatThrownBy(() -> immutable.add("d"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("disjoint() returns true when collections share no elements")
        void disjoint() {
            Set<Integer> a = new HashSet<>(List.of(1, 2, 3));
            Set<Integer> b = new HashSet<>(List.of(4, 5, 6));
            Set<Integer> c = new HashSet<>(List.of(3, 4, 5));
            assertThat(Collections.disjoint(a, b)).isTrue();
            assertThat(Collections.disjoint(a, c)).isFalse();
        }
    }
}
