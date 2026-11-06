package com.vbforge.practice.practice_10_hash_map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HashMap basics: put / get and the three ways to iterate - keys, values, entries.
 */
public class HashMapBasicsDemo {

    public static void main(String[] args) {

        Person donDraper = new Person("Don Draper", 20);
        Person peggyOlson = new Person("Peggy Olson", 25);
        Person bertCooper = new Person("Bert Cooper", 22);

        // Map<K, V> has two type parameters: key type and value type
        Map<String, Person> personByName = new HashMap<>();

        // put takes both a key and a value
        personByName.put(donDraper.getName(), donDraper);
        personByName.put(peggyOlson.getName(), peggyOlson);
        personByName.put(bertCooper.getName(), bertCooper);

        // lookup uses hashCode/equals of the key
        System.out.println("Don is " + personByName.get("Don Draper"));

        // foreach key
        for (String name : personByName.keySet()) {
            System.out.println(name);
        }

        // foreach value
        for (Person person : personByName.values()) {
            System.out.println(person);
        }

        // foreach entry
        for (Entry<String, Person> entry : personByName.entrySet()) {
            System.out.println(entry);
        }
    }
}
