package com.vbforge.practice.practice_6_generic_interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic interface + functional interface combination: {@link Storage} defines the
 * generic store, {@link Processor} is a single-method interface, and clients
 * parameterize both with the same T.
 */
public class GenericStorageDemo {

    public static void main(String[] args) {

        // Integer storage
        Storage<Integer> intStorage = new GenericStorage<>();
        intStorage.add(10);
        intStorage.add(20);
        intStorage.add(30);

        System.out.println("Integer Storage:");
        intStorage.print();

        intStorage.forEach(i -> System.out.println("Doubled: " + (i * 2)));

        // String storage
        Storage<String> stringStorage = new GenericStorage<>();
        stringStorage.add("java");
        stringStorage.add("generics");
        stringStorage.add("fun");

        System.out.println("\nString Storage:");
        stringStorage.forEach(s -> System.out.println("Uppercase: " + s.toUpperCase()));

        // Double storage with sum
        Storage<Double> doubleStorage = new GenericStorage<>();
        doubleStorage.add(1.5);
        doubleStorage.add(2.25);
        doubleStorage.add(3.75);

        final double[] sum = {0.0};
        doubleStorage.forEach(d -> sum[0] += d);
        System.out.println("\nSum of doubles: " + sum[0]);
    }

    public interface Storage<T> {

        void add(T item);

        T get(int index);

        void print();

        void forEach(Processor<T> processor);
    }

    @FunctionalInterface
    public interface Processor<T> {

        void process(T item);

    }

    public static class GenericStorage<T> implements Storage<T> {

        private final List<T> storage;

        public GenericStorage() {
            this.storage = new ArrayList<>();
        }

        @Override
        public void add(T item) {
            if (item != null) {
                storage.add(item);
            } else {
                throw new NullPointerException("Null value is not allowed");
            }
        }

        @Override
        public T get(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index must not be negative");
            } else if (index >= storage.size()) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            return storage.get(index);
        }

        @Override
        public void print() {
            for (T item : storage) {
                System.out.print(item + " ");
            }
            System.out.println();
        }

        @Override
        public void forEach(Processor<T> processor) {
            for (T item : storage) {
                processor.process(item);
            }
        }
    }
}
