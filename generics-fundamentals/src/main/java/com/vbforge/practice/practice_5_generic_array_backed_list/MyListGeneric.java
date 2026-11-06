package com.vbforge.practice.practice_5_generic_array_backed_list;

public interface MyListGeneric<T> {
    boolean isEmpty();

    int size();

    void add(T o);

    T get(int index);

    T set(int index, T newObject);

    void clear();

    void remove(int index);
}
