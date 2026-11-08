package com.vbforge.practice.practice_4_object_graphs;

import com.vbforge.practice.practice_1_serialization_helper.SerializationHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Write + read = a deep copy: no object of the graph is shared with the original (slow, but simple). */
public class DeepCopyDemo {

    static class Team implements Serializable {
        private static final long serialVersionUID = 1L;
        final List<String> members = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {
        Team original = new Team();
        original.members.add("Ann");
        original.members.add("Bob");

        Team copy = SerializationHelper.roundTrip(original);
        original.members.add("Carl");

        System.out.println("original: " + original.members);
        System.out.println("copy:     " + copy.members);
        System.out.println("lists are different objects: " + (original.members != copy.members));
    }
}
