package com.vbforge.practice.list.practice_4_custom_doubly_linked_list;

import java.util.NoSuchElementException;

/**
 * Custom doubly-linked list with FORWARD and REVERSE iterators.
 *
 * Each {@link DoublyNode} carries prev/next references, which makes both
 * directional traversal and O(1) removal-at-known-node possible. The
 * {@link DoublyIterator} is the minimal "cursor with direction" pattern —
 * {@code next()} walks either {@code current.next} or {@code current.prev}
 * depending on the {@code reverse} flag captured at construction time.
 */
public class DoublyLinkedListDemo {

    public static void main(String[] args) {

        MyDoublyLinkedList<String> dll = new MyDoublyLinkedList<>();

        dll.add("A");
        dll.add("B");
        dll.add("C");
        dll.addFirst("Start");
        dll.add("D");

        System.out.print("Forward: ");
        dll.printForward();

        System.out.print("Backward: ");
        dll.printBackward();

        dll.remove("B");
        System.out.print("After removing 'B': ");
        dll.printForward();

        System.out.println("Size: " + dll.size());
        System.out.println("Element at index 2: " + dll.get(2));

        // Using forward iterator
        System.out.print("Iterating forward: ");
        var it = dll.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }

        // Using reverse iterator
        System.out.print("\nIterating backward: ");
        var revIt = dll.reverseIterator();
        while (revIt.hasNext()) {
            System.out.print(revIt.next() + " ");
        }

        System.out.println("\nClearing list...");
        dll.clear();
        System.out.println("Is empty? " + dll.isEmpty());

    }

    // ─────────────────────────────────────────────────────────────
    // Node
    // ─────────────────────────────────────────────────────────────
    static class DoublyNode<T> {

        T data;
        DoublyNode<T> prev;
        DoublyNode<T> next;

        public DoublyNode(T data) {
            this.data = data;
        }

    }

    // ─────────────────────────────────────────────────────────────
    // Iterator — forward by default, reverse via flag
    // ─────────────────────────────────────────────────────────────
    static class DoublyIterator<T> {

        private DoublyNode<T> current;
        private final boolean reverse;

        public DoublyIterator(DoublyNode<T> startNode) {
            this(startNode, false);
        }

        public DoublyIterator(DoublyNode<T> startNode, boolean reverse) {
            this.current = startNode;
            this.reverse = reverse;
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            T data = current.data;
            current = reverse ? current.prev : current.next;
            return data;
        }

    }

    // ─────────────────────────────────────────────────────────────
    // The list itself
    // ─────────────────────────────────────────────────────────────
    static class MyDoublyLinkedList<T> {

        private DoublyNode<T> head;
        private DoublyNode<T> tail;
        private int size = 0;

        //add to end method:
        public void add(T data) {
            DoublyNode<T> newNode = new DoublyNode<>(data);
            if (head == null) {
                head = tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            size++;
        }

        //add to the beginning method:
        public void addFirst(T data) {
            DoublyNode<T> newNode = new DoublyNode<>(data);
            if (head == null) {
                head = tail = newNode;
            } else {
                newNode.next = head;
                head.prev = newNode;
                head = newNode;
            }
            size++;
        }

        //remove by value method:
        public boolean remove(T data) {
            DoublyNode<T> current = head;
            while (current != null) {
                if (current.data.equals(data)) {

                    //head
                    if(current.prev != null){
                        current.prev.next = current.next;
                    }else {
                        head = current.next;
                    }

                    //tail
                    if(current.next != null){
                        current.next.prev = current.prev;
                    }else {
                        tail = current.prev;
                    }

                    size--;
                    return true;

                }
                current = current.next;
            }
            return false;
        }

        // Get by index
        public T get(int index) {
            checkBounds(index);
            DoublyNode<T> current = head;
            for (int i = 0; i < index; i++) current = current.next;
            return current.data;
        }

        private void checkBounds(int index) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        }

        // Print forward
        public void printForward() {
            DoublyNode<T> current = head;
            while (current != null) {
                System.out.print(current.data + " <-> ");
                current = current.next;
            }
            System.out.println("null");
        }

        // Print backward
        public void printBackward() {
            DoublyNode<T> current = tail;
            while (current != null) {
                System.out.print(current.data + " <-> ");
                current = current.prev;
            }
            System.out.println("null");
        }

        public int size() {
            return size;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        // Clear the list
        public void clear() {
            head = tail = null;
            size = 0;
        }

        // Create a custom iterator
        public DoublyIterator<T> iterator() {
            return new DoublyIterator<>(head);
        }

        public DoublyIterator<T> reverseIterator() {
            return new DoublyIterator<>(tail, true);
        }

    }
}
