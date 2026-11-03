package com.vbforge.practice.practice_7_why_collections;

import java.util.Collection;
import java.util.List;

/**
 * Progression: why collections exist.
 *
 * <ol>
 *   <li>Hard-coded parameters - every new {@link Room} needs a code change.</li>
 *   <li>A {@code List<Room>} parameter - "add another room" becomes a one-line change.</li>
 *   <li>A {@code Collection<Room>} parameter - the caller may pass any collection type.</li>
 * </ol>
 */
public class WhyCollectionsDemo {

    public static void main(String[] args) {
        Room london       = new Room("London", "Presentation hall", 200, 500.0);
        Room manchester   = new Room("Manchester", "Living room", 5, 200.0);
        Room chesterfield = new Room("Chesterfield", "Living room", 7, 250.0);
        Room oxford       = new Room("Oxford", "Dining room", 17, 350.0);

        System.out.println("--- Step 1: hard-coded parameters ---");
        System.out.println("total = " + hardcodedTotal(london, manchester, chesterfield));   // 950.0

        System.out.println("\n--- Step 2: List<Room> parameter ---");
        System.out.println("total = " + listTotal(List.of(london, manchester, chesterfield, oxford)));   // 1300.0

        System.out.println("\n--- Step 3: Collection<Room> parameter ---");
        System.out.println("total = " + collectionTotal(List.of(london, manchester, chesterfield, oxford)));   // 1300.0
    }

    private static double hardcodedTotal(Room r1, Room r2, Room r3) {
        return r1.getPrice() + r2.getPrice() + r3.getPrice();
    }

    private static double listTotal(List<Room> rooms) {
        double total = 0;
        for (Room r : rooms) total += r.getPrice();
        return total;
    }

    private static double collectionTotal(Collection<Room> rooms) {
        return rooms.stream().mapToDouble(Room::getPrice).sum();
    }
}
