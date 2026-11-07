package com.vbforge.practice.map.practice_32_hashtable;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * HashMap vs Hashtable — side-by-side comparison.
 *
 * Highlights:
 *   - Hashtable does not allow null keys/values; HashMap does.
 *   - Hashtable is synchronized (thread-safe, but slower); HashMap is not.
 *   - Both use the same put/remove/entrySet API.
 *   - Hashtable is legacy (Java 1.0); prefer ConcurrentHashMap for modern
 *     thread-safe map needs.
 */
public class HashMapVsHashtableDemo {

    public static void main(String[] args) {
        //----------hashtable -------------------------
        Hashtable<Integer, String> ht = new Hashtable<Integer, String>();
        ht.put(101, " ajay");
        ht.put(101, "Vijay");
        ht.put(102, "Ravi");
        ht.put(103, "Rahul");
        System.out.println("-------------Hash table--------------");
        for (Map.Entry m : ht.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }

        //----------------hashmap--------------------------------
        HashMap<Integer, String> hm = new HashMap<Integer, String>();
        hm.put(100, "Amit");
        hm.put(104, "Amit");
        hm.put(101, "Vijay");
        hm.put(102, "Rahul");
        System.out.println("-----------Hash map-----------");
        for (Map.Entry m : hm.entrySet()) {
            System.out.println(m.getKey() + " " + m.getValue());
        }
    }
}
