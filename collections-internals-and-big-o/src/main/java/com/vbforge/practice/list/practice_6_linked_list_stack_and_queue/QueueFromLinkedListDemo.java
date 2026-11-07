package com.vbforge.practice.list.practice_6_linked_list_stack_and_queue;

public class QueueFromLinkedListDemo {
    public static void main(String[] args) throws EmptyQueueException {

        /*3)Implement a queue using a linked list: Explore how a linked list can be used to create the
         functionalities of stacks (LIFO - Last In First Out) or queues (FIFO - First In First Out) data structures.*/

        Queue queue = new Queue();

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        System.out.println(queue.dequeue()); // Output: 10
        System.out.println(queue.peek()); // Output: 20
        System.out.println(queue.isEmpty()); // Output: false
    }

    static class Node {
        int data;
        Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    static class Queue {
        Node front;  // Pointer to the front element (first element to be dequeued)
        Node rear;   // Pointer to the rear element (last element enqueued)

        public void enqueue(int data) {
            Node newNode = new Node(data);
            if (isEmpty()) {  // Handle the case of an empty queue
                front = rear = newNode;
            } else {
                rear.next = newNode;  // Add the new node to the end
                rear = newNode;        // Update the rear pointer
            }
        }

        public int dequeue() throws EmptyQueueException {
            if (isEmpty()) {
                throw new EmptyQueueException(); // Handle empty queue case
            }
            int data = front.data;
            if (front == rear) {  // Handle the case of removing the last element
                front = rear = null;
            } else {
                front = front.next;  // Move the front pointer
            }
            return data;
        }

        public boolean isEmpty() {
            return front == null;
        }

        public int peek() throws EmptyQueueException {
            if (isEmpty()) {
                throw new EmptyQueueException(); // Handle empty queue case
            }
            return front.data;
        }
    }

}

/*Queue implementation:
 Uses two pointers, front and rear:
 front points to the first element to be dequeued (removed).
 rear points to the last element that was enqueued (added).
 enqueue(int data):
 Creates a new node with the given data.
 If the queue is empty (isEmpty()), both front and rear point to the new node.
 Otherwise, appends the new node to the end of the queue:
 Sets the next pointer of the current rear to the new node.
 Updates the rear pointer to point to the newly added node.
 dequeue():
 Checks for an empty queue and throws an EmptyQueueException if so.
 Stores the data of the front element.
 If there's only one element (front == rear), sets both front and rear to null (empty queue).
 Otherwise, moves the front pointer to the next node.
 Returns the stored data (the element that was removed).
 isEmpty():
 Checks if the front pointer is null (indicating an empty queue).
 peek():
 Similar to dequeue(), but instead of removing the element, it returns the data of the front element.*/
