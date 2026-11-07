package com.vbforge.practice.practice_8_synchronized_map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Collections.synchronizedMap wraps every method call in synchronized(this), so single calls are safe.
 * Iteration is NOT covered: you must hold the map's lock for the whole loop
 * (easy to forget; without it another thread's change can cause ConcurrentModificationException).
 * The same rule holds for Collections.synchronizedList.
 * Compound check-then-act sequences also need the lock held externally.
 *
 * Prefer ConcurrentHashMap / CopyOnWriteArrayList when you can.
 */
public class SynchronizedMapIterationDemo {

    public static void main(String[] args) {

        Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());

        map.put("Java", 1);
        map.put("Python", 2);
        map.put("C++", 3);

        synchronized (map) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
        }

        // Iteration and removal example (Iterator.remove, still under the lock)
        synchronized (map) {
            Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Integer> entry = it.next();
                if (entry.getKey().startsWith("J")) {
                    it.remove();
                }
            }
        }
        System.out.println("After removing keys starting with J: " + map);
    }
}
