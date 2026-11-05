package com.vbforge.practice.practice_8_builtin_types_as_hash_keys;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** HashSet / HashMap rely on equals + hashCode; String and Integer override both, so duplicates collapse. */
public class BuiltInTypesAsHashKeysDemo {

    public static void main(String[] args) {
        Set<String> strings = new HashSet<>();
        strings.add("String-1");
        strings.add("String-1");
        strings.add("String-2");
        strings.add("String-3");
        System.out.println("strings size: " + strings.size() + "  <- String's own equals/hashCode dedupe");

        Map<Integer, String> intMap = new HashMap<>();
        intMap.put(1, "One");
        intMap.put(1, "Other");           // overwrites
        System.out.println("intMap: " + intMap + "  <- last write wins");

        Set<Integer> intSet = new HashSet<>();
        intSet.add(1);
        intSet.add(1);
        System.out.println("intSet: " + intSet + "  <- dedupe");
    }
}
