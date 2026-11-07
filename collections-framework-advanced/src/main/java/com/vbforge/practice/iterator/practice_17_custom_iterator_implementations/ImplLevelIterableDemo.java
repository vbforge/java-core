package com.vbforge.practice.iterator.practice_17_custom_iterator_implementations;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Custom Iterator variant #2 — {@code Iterable} is on the IMPL class, not
 * the interface.
 *
 * Lesson: If you don't want {@code Iterable} on your interface, put it on
 * the concrete impl class. For-each then only works when the reference type
 * is that impl (or a subtype) — NOT when the reference type is the bare
 * interface:
 *
 *   MyListIteratorImpl_2<String> list = new MyListIteratorImpl_2<>();  // OK for for-each
 *   MyListIterator_2<String> list = new MyListIteratorImpl_2<>();     // NOT OK for for-each
 *
 * The source comment ("have to use class=class, not an interface=class")
 * calls this out explicitly.
 *
 * Traversal is still reverse (see {@link ReverseIteratorDemo} for why).
 */
public class ImplLevelIterableDemo {

    public static void main(String[] args) {
        //have to use class=class, not an interface=class
        MyListIteratorImpl_2<String> list = new MyListIteratorImpl_2<>();
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
    // Interface: does NOT extend Iterable<T> on purpose
    // ─────────────────────────────────────────────────────────────
    //если убрать наследование от Iterable<T> , то нельзя использовать for each loop.
    //т.к. нет доступа к методу iterator();
    interface MyListIterator_2<T> {
        public boolean isEmpty();
        public int size();
        public void add(T o);
        public T get(int index);
        public T set(int index, T newObject);
        public void clear();
        public void remove(int index);
    }

    // ─────────────────────────────────────────────────────────────
    // Impl: also implements Iterable<T> — this is what enables for-each
    // ─────────────────────────────────────────────────────────────
    static class MyListIteratorImpl_2<T> implements MyListIterator_2<T>, Iterable<T> {
        private int capacity;
        private int size;
        private T[] data;

        public MyListIteratorImpl_2() {
            this(5);
        }

        public MyListIteratorImpl_2(int capacity) {
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
            return new MyIterator_2<>(this);
            // The source also demonstrates a two-step init variant:
            //   MyIterator_2 myIterator2 = new MyIterator_2();
            //   myIterator2.setMyListIterator(this);
            //   return myIterator2;
            // Kept as a comment only — the constructor form above is the clean one.
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            if (o.getClass() == MyListIteratorImpl_2.class) {
                MyListIteratorImpl_2 otherList = (MyListIteratorImpl_2) o;
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
    // Iterator — reverse traversal, raw impl reference
    // ─────────────────────────────────────────────────────────────
    static class MyIterator_2<T> implements Iterator<T> {

        private MyListIteratorImpl_2 myListIterator;

        private int size;
        private int iteratorStep;

        public MyIterator_2(MyListIteratorImpl_2 myListIterator) {
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
