package com.vbforge.practice.practice_9_hash_set;

import java.util.HashSet;
import java.util.Set;

/**
 * HashSet basics: add / contains / uniqueness / iteration, no index access.
 * Uniqueness relies on equals() and hashCode() of the element type (Car).
 */
public class HashSetBasicsDemo {

    public static void main(String[] args) {
        Car bmw = new Car("bmw", 1000);
        Car audi = new Car("audi", 2000);
        Car mercedes = new Car("mercedes", 3000);

        Set<Car> carSet = new HashSet<>();

        // add elements in the same way as a List
        carSet.add(bmw);
        carSet.add(audi);
        carSet.add(mercedes);
        System.out.println(carSet);

        System.out.println("bmw in set? " + carSet.contains(bmw));
        System.out.println("audi in set? " + carSet.contains(audi));
        System.out.println("mercedes in set? " + carSet.contains(mercedes));

        // Only stores unique members
        boolean added = carSet.add(mercedes);
        System.out.println("mercedes added again? " + added);
        System.out.println(carSet);
        System.out.println(carSet.size());

        // NB: hashCode/equals decide what "the same element" means

        // Other collections API stuff applies
        for (Car item : carSet) {
            System.out.println(item);
        }

        // No order, no index
        // carSet.get(0);   // compile error: Set has no get(int)
    }
}
