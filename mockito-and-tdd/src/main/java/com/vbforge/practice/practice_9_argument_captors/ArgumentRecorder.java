package com.vbforge.practice.practice_9_argument_captors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hand-written ArgumentCaptor. It stores REFERENCES, not copies.
 *   getValue()     the LAST captured value
 *   getAllValues() every value, in call order
 */
public class ArgumentRecorder<T> {

    private final List<T> values = new ArrayList<T>();

    public void capture(T value) { values.add(value); }

    public T getValue() {
        if (values.isEmpty()) {
            throw new IllegalStateException("nothing captured");
        }
        return values.get(values.size() - 1);
    }

    public List<T> getAllValues() { return Collections.unmodifiableList(values); }
}
