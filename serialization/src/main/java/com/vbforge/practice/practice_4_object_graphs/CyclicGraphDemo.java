package com.vbforge.practice.practice_4_object_graphs;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;

/** Cycles are fine: the stream stores a back-reference instead of recursing forever. */
public class CyclicGraphDemo {

    static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        final String name;
        Node next;

        Node(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) throws Exception {
        Node a = new Node("A");
        Node b = new Node("B");
        a.next = b;
        b.next = a; // cycle

        Node copy = SerializationHelper.roundTrip(a);

        System.out.println("copy: " + copy.name + " -> " + copy.next.name + " -> " + copy.next.next.name);
        System.out.println("cycle preserved (copy.next.next == copy): " + (copy.next.next == copy));
        System.out.println("copy is not the original: " + (copy != a));
    }
}
