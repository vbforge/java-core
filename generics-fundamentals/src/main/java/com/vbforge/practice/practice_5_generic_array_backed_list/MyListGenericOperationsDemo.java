package com.vbforge.practice.practice_5_generic_array_backed_list;

/**
 * The operations of the array-backed list: growth past the initial capacity,
 * get / set / remove / clear, bounds checks, and equals / hashCode that ignore capacity.
 */
public class MyListGenericOperationsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: growth past the initial capacity ==================
        MyListGeneric<String> list = new MyListGenericImpl<>(2);
        for (String s : new String[]{"a", "b", "c", "d", "e"}) {
            list.add(s);
        }
        System.out.println("after 5 adds (capacity started at 2): " + list + ", size = " + list.size());

        // ================== Scene 2: get / set / remove / clear ==================
        System.out.println("get(1) = " + list.get(1));
        System.out.println("set(1, \"B\") returns the old value: " + list.set(1, "B") + " -> " + list);
        list.remove(0);
        System.out.println("remove(0): " + list);
        list.clear();
        System.out.println("clear(): " + list + ", isEmpty = " + list.isEmpty());

        // ================== Scene 3: bounds are checked against size, not capacity ==================
        try {
            list.get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("get(0) on an empty list: " + e.getMessage());
        }

        // ================== Scene 4: equality ignores the capacity of the backing array ==================
        MyListGeneric<Integer> small = new MyListGenericImpl<>(2);
        MyListGeneric<Integer> large = new MyListGenericImpl<>(20);
        small.add(1);
        small.add(2);
        large.add(1);
        large.add(2);
        System.out.println("equals: " + small.equals(large) + ", same hashCode: " + (small.hashCode() == large.hashCode()));
    }
}
