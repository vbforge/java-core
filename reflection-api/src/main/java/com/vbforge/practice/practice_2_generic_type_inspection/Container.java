package com.vbforge.practice.practice_2_generic_type_inspection;

import java.util.List;

/**
 * Target for generic type inspection.
 * Generics are erased at runtime, but the parameterized type of a FIELD or METHOD
 * signature is kept in the class file (Signature attribute), so Field#getGenericType()
 * can recover List&lt;String&gt;.
 */
public class Container {

    private List<String> items;

    public Container(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }
}
