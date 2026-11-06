package com.vbforge.practice.practice_8_generic_methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generic methods - their own type parameter before the return type,
 * with type inference at the call site.
 */
public class GenericMethodBasicsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: printArray - one method for any reference-type array ==================
        Integer[] intArray = {1, 2, 3};
        String[] stringArray = {"Hello", "World"};
        Person[] personArray = {new Person("John", "Doe"), new Person("Jane", "Doe"), new Person("Jack", "Doe")};

        UtilityClass.printArray(intArray);
        UtilityClass.printArray(stringArray);
        UtilityClass.printArray(personArray);

        // ================== Scene 2: swap - in-place list mutation ==================
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        swap(list, 0, 3);
        System.out.println("After swap(0,3): " + list);  // [d, b, c, a]
    }

    // Swap two elements of a list in place
    public static <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }

    static class UtilityClass {

        public static <T> void printArray(T[] array) {
            System.out.println("Printing array of T class as: " + array.getClass().getSimpleName());
            for (T element : array) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }

    record Person(String firstName, String lastName) {
        @Override
        public String toString() {
            return String.format("[%s %s]", firstName, lastName);
        }
    }
}
