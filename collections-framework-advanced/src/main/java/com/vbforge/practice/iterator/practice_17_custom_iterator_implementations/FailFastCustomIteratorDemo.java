package com.vbforge.practice.iterator.practice_17_custom_iterator_implementations;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Custom Iterator variant #3 — a fail-fast iterator.
 *
 * The list tracks a {@code version} counter, incremented on every structural
 * change ({@code add}, {@code remove}, {@code clear}). When {@code iterator()}
 * is called, the iterator captures {@code version} and {@code size}. Each
 * {@code next()} verifies the live version still matches its snapshot —
 * if the list was modified externally mid-iteration, CME is thrown:
 *
 *   "versions are not same; iteratorVersion= 4 ;version= 5"
 *
 * This is the same mechanism {@code java.util.ArrayList.Itr} uses via
 * {@code modCount}, just spelled out longhand.
 *
 * Note: this custom impl only checks version in {@code next()}, not in
 * {@code hasNext()} — so a caller can see {@code hasNext() == true} on a
 * stale iterator and only find out at {@code next()} that it's dirty. Real
 * JDK iterators follow the same convention.
 */
public class FailFastCustomIteratorDemo {

    public static void main(String[] args) {
        MyListIterator3<String> list = new MyListIteratorImpl3<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        //Iterator<String> iteratorDefault = list.iterator();
        //while (iteratorDefault.hasNext()){
        //    String value = iteratorDefault.next();
        //    System.out.println(value);
        //}
        System.out.println("-------------------");
        try {
            for (String item : list) {
                if ("c".equals(item)) {
                    list.remove(2); // ConcurrentModificationException
                    // versions are not same; iteratorVersion= 4 ;version= 5
                    System.out.println(item);
                }
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("CME (expected): " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Interface — extends Iterable<T> for for-each
    // ─────────────────────────────────────────────────────────────
    //если убрать наследование от Iterable<T> , то нельзя использовать for each loop.
    //т.к. нет доступа к методу iterator();
    interface MyListIterator3<T> extends Iterable<T> {
        public boolean isEmpty();
        public int size();
        public void add(T o);
        public T get(int index);
        public T set(int index, T newObject);
        public void clear();
        public void remove(int index);
    }

    // ─────────────────────────────────────────────────────────────
    // Impl — version-tracked, iterator() returns a fail-fast iterator
    // ─────────────────────────────────────────────────────────────
    static class MyListIteratorImpl3<T> implements MyListIterator3<T> {
        private int capacity;
        private int size;
        private T[] data;
        private long version;

        public MyListIteratorImpl3() {
            this(5);
        }

        public MyListIteratorImpl3(int capacity) {
            this.data = (T[]) new Object[capacity];
            this.capacity = capacity;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public void add(T o) {
            if (size >= data.length) {
                T[] tmp = (T[]) new Object[data.length + 5];
                System.arraycopy(data, 0, tmp, 0, data.length);
                data = tmp;
            }
            data[size] = o;
            size++;
            version++;
        }

        @Override
        public T get(int index) {
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
            version++;
        }

        @Override
        public void remove(int index) {
            for (int i = index; i < size - 1; i++) {
                T rightValue = data[i + 1];
                data[i] = rightValue;
            }
            data[size - 1] = null;
            size--;
            version++;
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

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int iteratorStep;
                private long iteratorVersion = version;
                private int iteratorSize = size;

                @Override
                public boolean hasNext() {
                    return iteratorStep < iteratorSize;
                }

                @Override
                public T next() {
                    if (iteratorVersion != version) {
                        throw new ConcurrentModificationException("versions are not same; " +
                                "iteratorVersion= " + iteratorVersion + " ;version= " + version);
                    }
                    T item = get(iteratorStep);
                    iteratorStep++;
                    return item;
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() == MyListIteratorImpl3.class) {
                MyListIteratorImpl3 otherList = (MyListIteratorImpl3) o;
                Object[] otherData = otherList.data;
                return Arrays.equals(data, otherData);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }
}
