package com.vbforge.practice.list.practice_5_linked_list_challenges;

public class LinkedListChallengesDemo {
    public static void main(String[] args) {


        /**Challenging Operations:

         1)Find the middle element of a linked list: Implement a function that efficiently finds the middle element
         of a linked list, considering both even and odd-sized lists.*/
        MyLinkedListLocal list = new MyLinkedListLocal();
        System.out.println("1):");
        list.insertAtEnd(10);
        list.insertAtEnd(20);
        list.insertAtEnd(30);
        list.insertAtEnd(40);
        list.insertAtEnd(50);

        Node middleNode = list.findMiddleNode();
        System.out.println("Middle element: " + middleNode.data); // Output: 30



         /**2)Reverse a linked list: Write a function that reverses the order of elements in a linked list (iterative and
         recursive approaches can be explored).*/
        System.out.println("2):");
        list.printList(); //10 20 30 40 50
        System.out.println("a)Iterative:");
        list.reverseListIterative();
        list.printList(); //50 40 30 20 10
        System.out.println("b)Recursive:");
        list.reverseListRecursive();
        list.printList(); //10 20 30 40 50


         /**3)Detect a cycle in a linked list: Implement a function to determine if a linked list contains a cycle (where
         a node's next pointer points back to an earlier node in the list).*/
        System.out.println("3):");
        MyLinkedListLocal list2 = new MyLinkedListLocal();
        list2.head = new Node(1);
        list2.head.next = new Node(2);
        list2.head.next.next = new Node(3);
        list2.head.next.next.next = list2.head;  // Create a cycle

        if (list2.hasCycle()) {
            System.out.println("Cycle found");
        } else {
            System.out.println("No cycle found");
        }


         /**4)Merge two sorted linked lists: Write a function that merges two sorted linked lists into a single sorted
         linked list.*/
        System.out.println("4):");
        MyLinkedListLocal list3 = new MyLinkedListLocal();
        list3.head = new Node(1);
        list3.head.next = new Node(3);
        list3.head.next.next = new Node(5);

        MyLinkedListLocal list4 = new MyLinkedListLocal();
        list4.head = new Node(2);
        list4.head.next = new Node(4);
        list4.head.next.next = new Node(6);

        Node mergedHead = list3.mergeLists(list3.head, list4.head);

        MyLinkedListLocal mergedList = new MyLinkedListLocal();
        mergedList.head = mergedHead;

        mergedList.printList(); // Output: 1 2 3 4 5 6
    }

    static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    static class MyLinkedListLocal {
        Node head;

        // 1-exercise: Function to find the middle element of a linked list
        public Node findMiddleNode() {
            if (head == null) { // Handle empty list case
                return null;
            }

            Node slow = head;
            Node fast = head;

            // Move fast pointer twice as fast as slow pointer
            while (fast != null && fast.next != null) {
                slow = slow.next;
                fast = fast.next.next;
            }

            // Slow pointer will point to the middle node at the end
            return slow;
        }

        // 2-exercise:
        // a)Iterative (Function to reverse the linked list iteratively)
        public void reverseListIterative(){
            Node prev = null;
            Node current = head;
            while (current != null){
                Node nextNode = current.next; // Store the next node before changing links
                current.next = prev; // Reverse the link
                prev = current; // Move pointers
                current = nextNode;
            }
            head = prev; // Update head as new first node
        }
        // b)Recursive (Function to reverse the linked list recursively)
        public Node reverseRecursive(Node current){
            if (current == null || current.next == null) {
                return current; // Base case: return the last node (new head)
            }
            Node newHead = reverseRecursive(current.next);
            current.next.next = current; // Reverse link (next points to current)
            current.next = null; // Detach current node from the rest

            return newHead;

        }
        //to call from main:
        public void reverseListRecursive(){
            head = reverseRecursive(head);
        }

        // 3-exercise: Function to detect a cycle in the linked list
        public boolean hasCycle() {
            Node slow = head;
            Node fast = head;

            while (fast != null && fast.next != null) {
                slow = slow.next;  // Move slow pointer one step
                fast = fast.next.next;  // Move fast pointer two steps

                if (slow == fast) {  // If they meet, there's a cycle
                    return true;
                }
            }

            return false;  // No cycle found
        }

        // 4-exercise: Function to merge two sorted linked lists
        public Node mergeLists(Node list1, Node list2) {
            Node dummy = new Node(0);  // Create a dummy node for the merged list
            Node tail = dummy;  // Tail will keep track of the last node in the merged list

            while (list1 != null && list2 != null) {
                if (list1.data <= list2.data) {
                    tail.next = list1;
                    list1 = list1.next;
                } else {
                    tail.next = list2;
                    list2 = list2.next;
                }
                tail = tail.next;  // Advance the tail
            }

            // Append the remaining elements of list1 or list2 (if any)
            tail.next = (list1 != null) ? list1 : list2;

            return dummy.next;  // Return the head of the merged list (excluding the dummy node)
        }


        public void deleting(int value){
            if(head == null){ //Handle case if list is empty
                return;
            }
            if(head.data == value){ // Check if the head node needs to be deleted
                head = head.next; // Simply update head to point to the next node
                return;
            }
            // Traverse the list to find the node to be deleted
            Node current = head; //start
            while (current.next != null){
                if(current.next.data == value){ //if it`s found
                    current.next = current.next.next; //skipping node to deleting (update current.next)
                    return;
                }
                current = current.next;
            }
            // Value not found (no node to delete)
            System.out.println("Value " + value + " not found in the list.");
        }


        public void insertAtBeginning(int data){
            Node newNode = new Node(data);
            newNode.next = head; //Make the new node point to the current head
            head = newNode; //Update head to point of the new node
        }

        public void insertAtEnd(int data){
            Node newNode = new Node(data);
            if(head == null){ // If the list is empty, make the new node the head
                head = newNode;
                return;
            }
            Node current = head;
            while (current.next != null){ // Traverse to the last node
                current = current.next;
            }
            current.next = newNode; // Make the last node point to the new node
        }

        public void printList(){
            Node current = head;
            while (current != null){
                System.out.print(current.data + " ");
                current = current.next;
            }
            System.out.println();
        }
    }

}
