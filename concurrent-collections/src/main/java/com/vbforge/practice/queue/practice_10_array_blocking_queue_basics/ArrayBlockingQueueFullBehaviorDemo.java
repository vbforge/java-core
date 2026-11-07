package com.vbforge.practice.queue.practice_10_array_blocking_queue_basics;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * What a FULL bounded queue does:
 *   add(e)   throws IllegalStateException("Queue full")
 *   offer(e) returns false immediately
 *   put(e)   would block until there is room (see practice_11 for back-pressure)
 */
public class ArrayBlockingQueueFullBehaviorDemo {

    static class Person {
        private final int id;

        Person(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Person{id=" + id + '}';
        }
    }

    public static void main(String[] args) {
        Person person1 = new Person(1);
        Person person2 = new Person(2);
        Person person3 = new Person(3);
        Person person4 = new Person(4);

        // offer: capacity 2
        Queue<Person> personQueue = new ArrayBlockingQueue<>(2);
        System.out.println(personQueue.offer(person3)); // true
        System.out.println(personQueue.offer(person2)); // true
        System.out.println(personQueue.offer(person4)); // false (full)
        System.out.println(personQueue.offer(person1)); // false (full)
        System.out.println("Queue still holds: " + personQueue);

        // add on the same full queue
        try {
            personQueue.add(person1);
        } catch (IllegalStateException e) {
            System.out.println("add: IllegalStateException: " + e.getMessage());
        }
    }
}
