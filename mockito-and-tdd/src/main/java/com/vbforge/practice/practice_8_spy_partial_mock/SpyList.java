package com.vbforge.practice.practice_8_spy_partial_mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Hand-written spy: a REAL list whose get() can be overridden for chosen indexes.
 * Everything else runs the real code.
 */
public class SpyList extends ArrayList<String> {

    private static final long serialVersionUID = 1L;

    private final Map<Integer, String> overrides = new HashMap<Integer, String>();
    private int getCalls;

    /** doReturn(value).when(spy).get(index): registers the override WITHOUT running the real get(). */
    public SpyList doReturn(int index, String value) {
        overrides.put(index, value);
        return this;
    }

    public int getCalls() { return getCalls; }

    @Override
    public String get(int index) {
        getCalls++;
        if (overrides.containsKey(index)) {
            return overrides.get(index);
        }
        return super.get(index);
    }
}
