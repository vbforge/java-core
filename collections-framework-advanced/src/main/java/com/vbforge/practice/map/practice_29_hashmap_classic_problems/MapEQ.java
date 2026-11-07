package com.vbforge.practice.map.practice_29_hashmap_classic_problems;

import java.util.HashMap;
import java.util.Map;

/**
 * Puzzle: how many entries does this map hold?
 *
 * t1 and t2 have the same hashCode (9) AND are equals (because the custom
 * equals compares day references, and String literals are interned) — so
 * t2 overwrites t1's value. t3 has hashCode 9 too but is NOT equal to t1/t2,
 * so it lands in the same bucket as a separate entry.
 * Result: size == 2.
 */
public class MapEQ {
    public static void main(String[] args) {

        Map<ToDos, String> m = new HashMap<ToDos, String>();
        ToDos t1 = new ToDos("Monday");
        ToDos t2 = new ToDos("Monday");
        ToDos t3 = new ToDos("Tuesday");
        m.put(t1, "doLaundry");
        m.put(t2, "payBills");
        m.put(t3, "cleanAttic");
        System.out.println(m.size());
    }
    static class ToDos {
        String day;

        ToDos(String d) {
            day = d;
        }

        public boolean equals(Object o) {
            return ((ToDos) o).day == this.day;
        }

        public int hashCode() { return 9; }
    }
}
