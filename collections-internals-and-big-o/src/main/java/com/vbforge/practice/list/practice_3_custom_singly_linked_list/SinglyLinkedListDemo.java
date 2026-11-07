package com.vbforge.practice.list.practice_3_custom_singly_linked_list;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom singly-linked list — two implementation variants side by side.
 *
 *   Scene 1: {@code MyLinkedList<T>} — generic version, rich API
 *            (add, remove-by-value, get, reverse, getMiddle, hasLoop, removeDuplicates).
 *
 *   Scene 2: {@code IntLinkedList} — int-only version, remove-by-INDEX
 *            (from the {@code mylinkedlist2} source). Kept as a second scene
 *            because it demonstrates the sibling operation to Scene 1's
 *            remove-by-value — the two together cover both deletion shapes.
 *
 * Both variants are singly-linked and grow by appending to tail via traversal;
 * neither maintains a tail pointer, which is why add() is O(n).
 */
public class SinglyLinkedListDemo {

    public static void main(String[] args) {
        genericVariantScene();
        intVariantScene();
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 — generic MyLinkedList (mylinkedlist)
    // ─────────────────────────────────────────────────────────────
    private static void genericVariantScene() {
        System.out.println("=== Scene 1: generic MyLinkedList ===");

        MyLinkedList<Integer> list = new MyLinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(2);
        list.add(4);
        System.out.print("Original list: ");
        list.printList();

        list.remove(2);
        System.out.print("After removing 2 (first occurrence): ");
        list.printList();

        System.out.println("Size: " + list.size());
        System.out.println("Element at index 2: " + list.get(2));
        System.out.println("Middle element: " + list.getMiddle());

        list.reverse();
        System.out.print("After reversing: ");
        list.printList();

        list.removeDuplicates();
        System.out.print("After removing duplicates: ");
        list.printList();

        System.out.println("Contains loop? " + list.hasLoop());
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 — int-only LinkedListImplementation (mylinkedlist2)
    // ─────────────────────────────────────────────────────────────
    private static void intVariantScene() {
        System.out.println("\n=== Scene 2: int-only IntLinkedList ===");

        IntLinkedList list = new IntLinkedList();
        list.add(1);
        list.add(3);
        list.add(5);

        System.out.println(list);

        // get(1) -- no exception here
        System.out.println(list.get(1));

        // remove by index
        list.remove(0);
        System.out.println(list);
    }

    // ─────────────────────────────────────────────────────────────
    // Scene 1 support: generic MyLinkedList
    // ─────────────────────────────────────────────────────────────
    static class Node<T> {

        T data;
        Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    static class MyLinkedList<T> {

        private Node<T> head;

        //add to end method
        public void add(T data){
            Node<T> newNode = new Node<>(data);
            if(head == null){
                head = newNode;
                return;
            }
            Node<T> current = head;
            while (current.next != null){
                current = current.next;
            }
            current.next = newNode;
        }

        //print all elements:
        public void printList(){
            Node<T> current = head;
            while (current != null){
                System.out.print(current.data + "->");
                current = current.next;
            }
            System.out.println("null");
        }

        //remove by value
        public void remove(T data){
            if(head == null){
                return;
            }

            if(head.data.equals(data)){
                head = head.next;
                return;
            }

            Node<T> current = head;
            while (current.next != null && !current.next.data.equals(data)){
                current = current.next;
            }

            if(current.next != null){
                current.next = current.next.next;
            }
        }

        //size method:
        public int size(){
            int size = 0;
            Node<T> current = head;
            while (current != null){
                size++;
                current = current.next;
            }
            return size;
        }

        //get by index method:
        public T get(int index){
            int counter = 0;
            Node<T> current = head;
            while (current != null){
                if(counter == index){
                    return current.data;
                }
                current = current.next;
                counter++;
            }
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }

        //reverse the list method:
        public void reverse(){
            Node<T> prev = null;
            Node<T> current = head;
            Node<T> next = null;
            while (current != null) {
                next = current.next;
                current.next = prev;
                prev = current;
                current = next;
            }
            head = prev;
        }

        //find the middle element:
        public T getMiddle(){
            if(head == null){
                return null;
            }
            Node<T> slow = head;
            Node<T> fast = head;
            while (fast != null && fast.next != null){
                slow = slow.next;
                fast = fast.next.next;
            }
            return slow.data;
        }

        //detect loop method (Floyd`s Cycle detection)
        public boolean hasLoop(){
            Node<T> slow = head;
            Node<T> fast = head;
            while (fast != null && fast.next != null){
                slow = slow.next;
                fast = fast.next.next;
                if(slow == fast){
                    return true;
                }
            }
            return false;
        }

        //remove duplicates method (unordered list)
        public void removeDuplicates(){
            Set<T> seen = new HashSet<>();
            Node<T> current = head;
            Node<T> previous = null;
            while(current != null){
                if(seen.contains(current.data)){
                    previous.next = current.next;
                }else{
                    seen.add(current.data);
                    previous = current;
                }
                current = current.next;
            }
        }

    }

    // ─────────────────────────────────────────────────────────────
    // Scene 2 support: int-only IntLinkedList
    // ─────────────────────────────────────────────────────────────
    static class IntLinkedList {

        private IntNode head;
        private int size;

        public void add(int value){
            //first adding to the list
            if(head == null){
                this.head = new IntNode(value);
            }else {
                IntNode tmp = head;
                while (tmp.getNext() != null){
                    tmp = tmp.getNext();
                }
                tmp.setNext(new IntNode(value));
                // head[1] -> [2] -> [3] -> add [4]
            }
            size++;
        }

        public String toString(){
            int [] result = new int [size];
            int index = 0;
            IntNode tmp = head;
            while (tmp != null){
                result[index++] = tmp.getValue();
                tmp = tmp.getNext();
            }

            return Arrays.toString(result);
        }

        public void remove(int index){
            //1-2-3 need remove index1
            //have to change reference for 1 to 3
            if(index == 0){
                head = head.getNext();
                size--;
                return;
            }
            int currentIndex = 0;
            IntNode tmp = head;
            while (tmp != null){
                if (currentIndex == index - 1){
                    tmp.setNext(tmp.getNext().getNext());
                    size--;
                    return;
                }else {
                    tmp = tmp.getNext();
                    currentIndex++;
                }
            }
        }

        public int get(int index){
            int currentIndex = 0;
            IntNode tmp = head;
            while (tmp != null){
                if(currentIndex == index){
                    return tmp.getValue();
                }else {
                    tmp = tmp.getNext();
                    currentIndex++;
                }
            }
            // 1-2-3
            //need 1
            throw new IllegalArgumentException();
        }

        private static class IntNode {
            private int value;
            private IntNode next;

            public IntNode(int value) {
                this.value = value;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public IntNode getNext() {
                return next;
            }

            public void setNext(IntNode next) {
                this.next = next;
            }
        }
    }
}
