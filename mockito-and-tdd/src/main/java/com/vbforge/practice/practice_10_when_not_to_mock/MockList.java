package com.vbforge.practice.practice_10_when_not_to_mock;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/** What mock(List.class) behaves like: it records add(), but stores nothing. */
public class MockList extends AbstractList<String> {

    private final List<String> recordedAdds = new ArrayList<String>();

    @Override public boolean add(String element) { recordedAdds.add(element); return true; }
    @Override public int size() { return 0; }
    @Override public String get(int index) { return null; }

    public List<String> recordedAdds() { return recordedAdds; }
}
