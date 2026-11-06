package com.vbforge.practice.practice_13_wildcard_lower_bound;

import java.util.ArrayList;
import java.util.List;

/**
 * Lower-bounded wildcard {@code ? super RedCat} over a name-based Animal hierarchy:
 *   Animal <- Cat <- RedCat
 *   Animal <- Dog
 * <p>
 *   - accepts List&lt;RedCat&gt;, List&lt;Cat&gt;, List&lt;Animal&gt; (RedCat and its supertypes)
 *   - WRITE is fine (RedCat and its subtypes go in); READ comes back as Object only
 *   - CONSUMER - consumes RedCat objects into the list
 */
public class LowerBoundedWildcardDemo {

    public static void main(String[] args) {

        List<Animal> animalList = new ArrayList<>();
        List<Cat> catList = new ArrayList<>();
        List<RedCat> redCatList = new ArrayList<>();
        List<Dog> dogList = new ArrayList<>();

        addCat(animalList);   // list of a supertype of Cat
        addCat(catList);      // list of Cat
        addCat(redCatList);   // list of RedCat itself
        // addCat(dogList);   // COMPILE ERROR: Dog is not a supertype of RedCat

        System.out.println("animals: " + animalList + ", cats: " + catList
                + ", redCats: " + redCatList + ", dogs: " + dogList);
    }

    private static void addCat(List<? super RedCat> catListRed) {
        catListRed.add(new RedCat("red"));
        Object last = catListRed.get(catListRed.size() - 1);  // reads come back as Object, not RedCat
        // RedCat back = catListRed.get(0);                   // COMPILE ERROR
        System.out.println("Cat Added: " + last);
    }

    static class Animal {

        String name;

        Animal(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class Cat extends Animal {
        Cat(String name) {
            super(name);
        }
    }

    static class Dog extends Animal {
        Dog(String name) {
            super(name);
        }
    }

    static class RedCat extends Cat {
        RedCat(String name) {
            super(name);
        }
    }
}
