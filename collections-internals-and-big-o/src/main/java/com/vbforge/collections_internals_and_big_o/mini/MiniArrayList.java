package com.vbforge.collections_internals_and_big_o.mini;

import java.util.Arrays;

/**
 * A stripped-down re-implementation of {@link java.util.ArrayList}'s storage strategy, with
 * COUNTERS that make the hidden costs visible.
 *
 * <p>The real {@code ArrayList} keeps its array private (and on Java 17+ reflection cannot open
 * it), so this class mirrors the algorithm instead:
 * <ul>
 *   <li>the array starts EMPTY and is allocated with capacity 10 on the first {@code add}</li>
 *   <li>when full, the capacity grows by 50 %: {@code newCapacity = old + (old >> 1)}</li>
 *   <li>inserting/removing in the middle shifts every element behind the index</li>
 * </ul>
 */
public class MiniArrayList<E> {

    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elementData = new Object[0];
    private int size;

    private int shiftCount;   // elements moved by add(index, e) / remove(index)
    private int copyCount;    // elements copied into a bigger array while growing
    private int growCount;    // (re)allocations of the backing array, including the first one

    public boolean add(E element) {
        ensureCapacity(size + 1);
        elementData[size++] = element;
        return true;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        int shifted = size - index;
        System.arraycopy(elementData, index, elementData, index + 1, shifted);
        shiftCount += shifted;
        elementData[index] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elementData[index];
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E old = (E) elementData[index];
        int shifted = size - index - 1;
        System.arraycopy(elementData, index + 1, elementData, index, shifted);
        shiftCount += shifted;
        elementData[--size] = null;     // let the garbage collector reclaim the element
        return old;
    }

    public int size() {
        return size;
    }

    /** Length of the backing array — the number of elements that fit WITHOUT growing. */
    public int capacity() {
        return elementData.length;
    }

    public int shiftCount() {
        return shiftCount;
    }

    public int copyCount() {
        return copyCount;
    }

    public int growCount() {
        return growCount;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length) {
            grow(minCapacity);
        }
    }

    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity == 0
                ? Math.max(DEFAULT_CAPACITY, minCapacity)
                : Math.max(oldCapacity + (oldCapacity >> 1), minCapacity);
        copyCount += size;
        growCount++;
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
}
