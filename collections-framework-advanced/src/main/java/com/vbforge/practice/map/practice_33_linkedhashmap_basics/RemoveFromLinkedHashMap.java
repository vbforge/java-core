package com.vbforge.practice.map.practice_33_linkedhashmap_basics;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class RemoveFromLinkedHashMap {
    public static void main(String[] args) {

        Map<String, Integer> map = new LinkedHashMap<>();

        map.put("BMW", 220);
        map.put("Mercedes", 250);
        map.put("Ford", 230);
        map.put("Audi", 190);
        map.put("Volvo", 160);

        // 1. Remove by key
        map.remove("BMW");
        System.out.println("After removing BMW: " + map);

        // 2. Remove by key only if value matches
        boolean audiRemoved = map.remove("Audi", 190);
        System.out.println("Does removed Audi with value 190? " + audiRemoved);
        System.out.println("After removing Audi: " + map);

        // 3. Safe removal using Iterator
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            //condition for deleting:
            if(iterator.next().getValue() < 200){
                iterator.remove();
            }
        }
        System.out.println("After removing cars with value < 200 (iterator based): " + map);

        // 4. Remove using removeIf() Java 8+
        //update map for more entries:
        map.put("Ferrari", 280);
        map.put("Lamborghini", 350);
        map.entrySet().removeIf(entry -> entry.getValue() < 270);     //getValue() is the condition
        System.out.println("After removing cars with value < 270 (removeIf): " + map);

        map.entrySet().removeIf(entry -> entry.getKey().startsWith("F"));
        System.out.println("After removing cars starting with 'F': " + map); //expected Lamborghini

    }
}
