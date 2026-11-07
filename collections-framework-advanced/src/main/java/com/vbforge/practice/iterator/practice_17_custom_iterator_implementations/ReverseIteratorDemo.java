package com.vbforge.practice.iterator.practice_17_custom_iterator_implementations;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Custom Iterator variant #1 — reverse traversal on a hand-rolled list.
 *
 * Key lesson (from the source comments):
 *   - The interface MUST extend {@code Iterable<T>} for for-each to work.
 *     If you drop the {@code Iterable} super-interface, the compiler cannot
 *     find {@code iterator()} at the for-each site → for-each is unavailable.
 *
 *   - {@code MyIterator.next()} walks from the END of the backing array
 *     ({@code (size-1)-iteratorStep}) — so both iteration styles print
 *     "d c b a" rather than "a b c d". That is deliberate: the traversal
 *     strategy is a property of the Iterator, not of the list.
 */
public class ReverseIteratorDemo {

    public static void main(String[] args) {
        MyListIterator<String> list = new MyListIteratorImpl<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        Iterator<String> iteratorDefault = list.iterator();
        while (iteratorDefault.hasNext()) {
            String value = iteratorDefault.next();
            System.out.println(value);
        }
        System.out.println("-------------------");
        for (String item : list) {
            System.out.println(item);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Interface: extends Iterable<T> so for-each is available
    // ─────────────────────────────────────────────────────────────
    //если убрать наследование от Iterable<T> , то нельзя использовать for each loop.
    //т.к. нет доступа к методу iterator();
    interface MyListIterator<T> extends Iterable<T> {
        public boolean isEmpty();
        public int size();
        public void add(T o);
        public T get(int index);
        public T set(int index, T newObject);
        public void clear();
        public void remove(int index);
    }

    // ─────────────────────────────────────────────────────────────
    // Impl: array-backed, iterator() returns MyIterator
    // ─────────────────────────────────────────────────────────────
    static class MyListIteratorImpl<T> implements MyListIterator<T> {
        private int capacity;
        private int size;
        private T[] data;

        public MyListIteratorImpl() {
            this(5);
        }

        public MyListIteratorImpl(int capacity) {
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
        }

        @Override
        public void remove(int index) {
            for (int i = index; i < size - 1; i++) {
                T rightValue = data[i + 1];
                data[i] = rightValue;
            }
            data[size - 1] = null;
            size--;
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
            return new MyIterator<>(this);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() == MyListIteratorImpl.class) {
                MyListIteratorImpl otherList = (MyListIteratorImpl) o;
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

    // ─────────────────────────────────────────────────────────────
    // The custom Iterator — reverse traversal, raw impl reference preserved
    // ─────────────────────────────────────────────────────────────
    static class MyIterator<T> implements Iterator<T> {

        private MyListIteratorImpl myListIterator;

        private int size;
        private int iteratorStep;

        public MyIterator(MyListIteratorImpl myListIterator) {
            this.myListIterator = myListIterator;
            this.size = myListIterator.size();
        }

        @Override
        public boolean hasNext() {
            if (iteratorStep < size) {
                return true;
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            //Object item = myListIterator.get(iteratorStep);// variant1
            Object item = myListIterator.get((size - 1) - iteratorStep);// variant1 - revers show
            iteratorStep++;
            return (T) item;
        }
    }
}
