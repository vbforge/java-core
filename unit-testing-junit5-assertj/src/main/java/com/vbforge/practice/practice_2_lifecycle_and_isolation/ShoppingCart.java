package com.vbforge.practice.practice_2_lifecycle_and_isolation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Mutable object with state: the reason why every test needs a fresh instance. */
public class ShoppingCart {

    private final List<String> items = new ArrayList<>();

    public void add(String item) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("item must not be blank");
        }
        items.add(item);
    }

    public boolean remove(String item) {
        return items.remove(item);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Read-only view, callers cannot modify the cart through it. */
    public List<String> items() {
        return Collections.unmodifiableList(items);
    }

    public void clear() {
        items.clear();
    }
}
