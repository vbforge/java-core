package com.vbforge.practice.deque.practice_45_arraydeque_basics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Pop / peek semantics on a Deque used as a stack, plus the
 * null-vs-exception contrast for {@link Queue} peek/poll/remove/element.
 */
public class ArrayDequeAsStackDemo {
    public static void main(String[] args) {

        Deque<String> messageStack = new ArrayDeque<>();
        messageStack.push("Message1");
        messageStack.push("Message2");
        messageStack.push("Message3");
        messageStack.push("Message4");

        print(messageStack);

        System.out.println(messageStack.pop() + " pop");
        System.out.println(messageStack.pop() + " pop");
        print(messageStack);
        messageStack.push("Message5");
        System.out.println(messageStack.peek() + " peek");

        print(messageStack);

        //check exception:
        Queue<String> queue = new PriorityQueue<>();
        queue.peek(); //null if empty - no exception
        queue.poll(); //null if empty - no exception
//        queue.element(); //NoSuchElementException
//        queue.remove(); //NoSuchElementException

    }

    public static void print(Deque<String> deque) {

        System.out.format("%n--Deque Contents--%n");

        int x = 0;
        for(String msg : deque) {
            System.out.format("%x: %s %s %n", x++, msg, x == 1 ? "(Top)":"");
        }

        System.out.println("");

    }
}
