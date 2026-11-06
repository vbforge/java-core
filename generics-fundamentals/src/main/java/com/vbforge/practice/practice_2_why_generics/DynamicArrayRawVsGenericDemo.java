package com.vbforge.practice.practice_2_why_generics;

import java.util.Arrays;

/**
 * A hand-written dynamic array before and after generics.
 * The Object-based version accepts anything and forces casts on read;
 * the generic version fixes the element type per instance.
 */
public class DynamicArrayRawVsGenericDemo {

    public static void main(String[] args) {

        // ================== Scene 1: DynamicArray before generics ==================
        System.out.println("--- DynamicArray: before generics ---");
        DynamicArrayRaw dynArray = new DynamicArrayRaw();
        dynArray.addElement(Integer.valueOf(10));
        dynArray.addElement("Java");               // compiles: any Object is accepted
        Integer first = (Integer) dynArray.getElement(0);
        String second = (String) dynArray.getElement(1);
        System.out.println(first);
        System.out.println(second);

        // ================== Scene 2: DynamicArray with generics ==================
        System.out.println("\n--- DynamicArray: with generics ---");
        DynamicArrayGeneric<Integer> genericDyn = new DynamicArrayGeneric<>();
        genericDyn.addElement(10);
        genericDyn.addElement(20);
        Integer firstGen = genericDyn.getElement(0);   // no cast
        System.out.println(firstGen);
        System.out.println(genericDyn.getElement(1));
        // genericDyn.addElement("Java"); // compile error - type safety!
    }

    // -------- helper classes --------
    static class DynamicArrayRaw {
        private Object[] elements = new Object[5];
        private int size;

        public void addElement(Object element) {
            if (size == elements.length) {
                elements = Arrays.copyOf(elements, size * 2);
            }
            elements[size++] = element;
        }

        public Object getElement(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            return elements[index];
        }
    }

    static class DynamicArrayGeneric<T> {
        // T is erased, so a T[] cannot be created directly: Object[] + cast (the array never leaves this class)
        @SuppressWarnings("unchecked")
        private T[] elements = (T[]) new Object[5];
        private int size;

        public void addElement(T element) {
            if (size == elements.length) {
                elements = Arrays.copyOf(elements, size * 2);
            }
            elements[size++] = element;
        }

        public T getElement(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            return elements[index];
        }
    }
}
