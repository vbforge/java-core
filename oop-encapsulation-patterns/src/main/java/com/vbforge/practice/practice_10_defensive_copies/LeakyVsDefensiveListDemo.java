package com.vbforge.practice.practice_10_defensive_copies;

import java.util.ArrayList;
import java.util.List;

/**
 * A mutable List field leaks in two directions:
 *   IN : the constructor keeps the caller's list
 *   OUT: the getter hands out the internal list
 * Fix: copy IN, copy OUT.
 */
public class LeakyVsDefensiveListDemo {

    /** BAD: stores the caller's reference and returns its own. */
    static class LeakyPlayer {
        private final String name;
        private final int age;
        private final List<String> teams;

        LeakyPlayer(String name, int age, List<String> listOfTeams) {
            this.name = name;
            this.age = age;
            this.teams = listOfTeams;      // stores the caller's reference
        }

        List<String> getTeams() {
            return teams;                  // returns the internal reference
        }

        @Override
        public String toString() {
            return "Player{name='" + name + "', age=" + age + ", teams=" + teams + "}";
        }
    }

    /** GOOD: defensive copy on both sides. */
    static class Player {
        private final String name;
        private final int age;
        private final List<String> teams;

        Player(String name, int age, List<String> listOfTeams) {
            this.name = name;
            this.age = age;
            this.teams = new ArrayList<>(listOfTeams);   // copy IN
        }

        List<String> getTeams() {
            return new ArrayList<>(teams);               // copy OUT
        }

        @Override
        public String toString() {
            return "Player{name='" + name + "', age=" + age + ", teams=" + teams + "}";
        }
    }

    public static void main(String[] args) {
        System.out.println("-- BAD: no defensive copies --");
        List<String> leakyTeams = new ArrayList<>(List.of("MU", "Dynamo"));
        LeakyPlayer leaky = new LeakyPlayer("Vasyl", 41, leakyTeams);
        System.out.println("initial:                              " + leaky);

        leaky.getTeams().add("Niva");               // through the getter
        System.out.println("after getTeams().add(\"Niva\"):         " + leaky);

        leakyTeams.add("PSV");                      // through the original list
        System.out.println("after original.add(\"PSV\"):            " + leaky);

        System.out.println("\n-- GOOD: defensive copies --");
        List<String> teams = new ArrayList<>(List.of("MU", "Dynamo"));
        Player player = new Player("Vasyl", 41, teams);
        System.out.println("initial:                              " + player);

        player.getTeams().add("Niva");
        System.out.println("after getTeams().add(\"Niva\"):         " + player);

        teams.add("PSV");
        System.out.println("after original.add(\"PSV\"):            " + player);
    }
}
