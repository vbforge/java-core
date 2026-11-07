package com.vbforge.practice.map.practice_28_hashmap_basics;

import java.util.HashMap;
import java.util.Map;

public class TeamManager {
    public static void main(String[] args) {

        // Simulate a football team manager system using a Map:
        // Key = Player ID, Value = Player object
        Map<Integer, Player> playerMap = new HashMap<>();

        // Adding players to the team
        playerMap.put(1, new Player("Stankovic", 5, 74));
        playerMap.put(2, new Player("Ronaldo", 9, 65));
        playerMap.put(3, new Player("Recoba", 20, 63));

        // Overwriting player with ID 3 (simulating player data update)
        playerMap.put(3, new Player("Recoba", 20, 65)); // latest value will overwrite

        // Output the entire player map
        System.out.println("All registered players:");
        System.out.println(playerMap); // HashMap's toString

        // Basic Map operations
        System.out.println("\n--- Basic operations ---");
        System.out.println("Total players: " + playerMap.size());
        System.out.println("Player with ID 1: " + playerMap.get(1));
        System.out.println("Contains player ID 4? " + playerMap.containsKey(4));
        System.out.println("All player IDs: " + playerMap.keySet());
        System.out.println("All player entries: " + playerMap.entrySet());

        // Remove a player (e.g., transfer out)
        System.out.println("\n--- Removing player ID 3 (Recoba leaves the team) ---");
        playerMap.remove(3);
        System.out.println("Updated player list:");
        playerMap.entrySet().forEach(System.out::println);

        // Iterate using lambda (key + value)
        System.out.println("\n--- Iteration with lambda ---");
        playerMap.forEach((id, player) -> System.out.println("ID: " + id + " - Name: " + player.name()));

        // Default lookup: Trying to find a non-existing player with fallback
        System.out.println("\n--- Default value when player not found ---");
        Player defaultPlayer = playerMap.getOrDefault(3, new Player("Default", 100, 0.001));
        System.out.println("Lookup ID 3 (missing): " + defaultPlayer);

        // Print just the player list (values)
        System.out.println("\n--- All current players (values only) ---");
        System.out.println(playerMap.values());
    }

    // Record class for Player (name, jersey number, weight)
    record Player(String name, int number, double weight) {
    }
}
