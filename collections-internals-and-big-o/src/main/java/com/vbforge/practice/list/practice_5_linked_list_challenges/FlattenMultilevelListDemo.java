package com.vbforge.practice.list.practice_5_linked_list_challenges;

public class FlattenMultilevelListDemo {
    public static void main(String[] args) {

        /*2)Flatten a multilevel linked list: If you're comfortable with nested linked structures, this exercise
         involves converting a multilevel linked list (where nodes can have linked lists as data) into a single-level
         linked list.*/

        // Create a sample multilevel linked list
        Node head = new Node(1);
        head.child = new Node(3);
        head.child.next = new Node(5);
        head.next = new Node(2);
        head.next.child = new Node(4);

        LinkedList list = new LinkedList();
        list.head = head;

        System.out.println("Original multilevel list:");
        // Can print the multilevel structure here (implementation not shown for brevity)

        list.flattenList();

        System.out.println("Flattened list:");
        list.printList(); // Output: 1 3 5 2 4
    }

    static class Node {
        int data;
        Node next;
        Node child;

        public Node(int data) {
            this.data = data;
            this.next = null;
            this.child = null;
        }
    }
    static class LinkedList{
        Node head;

        // Function to flatten a multilevel linked list
        public void flattenList() {
            Node current = head;
            Node tail = head;

            while (current != null) {
                // If the current node has a child list
                if (current.child != null) {
                    // Connect the tail of the current list to the head of the child list
                    tail.next = current.child;

                    // Find the new tail by recursively flattening the child list
                    Node childTail = flattenListRecursive(current.child);

                    // Update the tail of the main list to point to the new child tail
                    tail = childTail;

                    // After processing the child, remove the child pointer from the current node
                    current.child = null;
                } else {
                    // Move to the next node if no child list is present
                    current = current.next;
                }
            }
        }

        // Helper function to flatten child lists recursively (optional)
        private Node flattenListRecursive(Node childHead) {
            Node current = childHead;
            Node tail = childHead;

            while (current != null) {
                if (current.child != null) {
                    tail = flattenListRecursive(current.child);
                } else {
                    current = current.next;
                }
                tail = tail.next;
            }

            return tail;
        }

        // Function to print the flattened linked list
        public void printList() {
            Node current = head;
            while (current != null) {
                System.out.print(current.data + " ");
                current = current.next;
            }
            System.out.println();
        }
    }

}
