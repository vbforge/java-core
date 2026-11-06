package com.vbforge.practice.practice_5_generic_array_backed_list;

import java.util.Objects;

/**
 * Array-backed list. T is erased, so the backing array is created as Object[]
 * and cast to T[] - safe here because the array never leaves this class.
 */
public class MyListGenericImpl<T> implements MyListGeneric<T> {
    private int size;
    private T[] data;

    public MyListGenericImpl() {
        this(5);
    }

    @SuppressWarnings("unchecked")
    public MyListGenericImpl(int capacity) {
        this.data = (T[]) new Object[capacity];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(T o) {
        if (size >= data.length) {
            T[] tmp = (T[]) new Object[data.length + 5];
            System.arraycopy(data, 0, tmp, 0, data.length);
            data = tmp;
        }
        data[size] = o;
        size++;
    }

    @Override
    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    @Override
    public T set(int index, T newObject) {
        T value = get(index);
        data[index] = newObject;
        return value;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @Override
    public void remove(int index) {
        checkIndex(index);
        for (int i = index; i < size - 1; i++) {
            T rightValue = data[i + 1];
            data[i] = rightValue;
        }
        data[size - 1] = null;
        size--;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public String toString() {
        String text = "[";
        for (int i = 0; i < size; i++) {
            text = text + data[i];
            if (i != size - 1) {
                text = text + ",";
            }
        }
        text = text + "]";
        return text;
    }

    // equals/hashCode look at the first `size` elements only: the unused tail of the
    // backing array (capacity) is not part of the list's value.
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() == MyListGenericImpl.class) {
            MyListGenericImpl<?> otherList = (MyListGenericImpl<?>) o;
            if (size != otherList.size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(data[i], otherList.data[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            result = 31 * result + Objects.hashCode(data[i]);
        }
        return result;
    }
}
