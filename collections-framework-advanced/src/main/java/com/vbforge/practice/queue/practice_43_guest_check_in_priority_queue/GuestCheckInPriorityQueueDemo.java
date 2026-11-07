package com.vbforge.practice.queue.practice_43_guest_check_in_priority_queue;

import com.vbforge.practice.common.practice_2_hotel_sample_data.Guest;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Same guest data as {@code GuestCheckInQueueDemo}, but PriorityQueue + loyalty
 * comparator — loyalty members jump to the head regardless of arrival order.
 */
public class GuestCheckInPriorityQueueDemo {
    public static void main(String[] args) {

        Guest john = new Guest("John", "Doe", false);
        Guest bob = new Guest("Bob", "Doe", false);
        Guest sonia = new Guest("Sonia", "Doe", true); //loyalty program
        Guest siri = new Guest("Siri", "Doe", true); //loyalty program

        //for PriorityQueue we need to build comparator, so we can define the priority
        Comparator<Guest> programComp = Comparator.comparing(Guest::isLoyaltyProgramMember).reversed();

        //create a priority queue
        Queue<Guest> checkInQueue = new PriorityQueue<>(programComp);

        //offer
        checkInQueue.offer(john); //adding element to the queue
        checkInQueue.offer(bob); //adding element to the queue
        checkInQueue.offer(sonia); //adding element to the queue
        checkInQueue.offer(siri); //adding element to the queue
        print(checkInQueue);

        //poll
        Guest guest = checkInQueue.poll();//retrieve and remove head
        print(checkInQueue);
        System.out.println(guest); //null if no elements in the queue

        //peek
        Guest guestPeeked = checkInQueue.peek();//retrieve only and not remove
        print(checkInQueue);
        System.out.println(guestPeeked);

    }
    /**
     * Prints the contents of the given queue of Guest objects to the console.
     * This method formats the output to display each Guest in the queue
     * along with its index. The first element of the queue is labeled as
     * the "Head" for clarity.
     *
     * @param queue The queue containing Guest objects to be printed.
     *              This queue should not be null.
     */
    public static void print(Queue<Guest> queue) {

        System.out.format("%n--Queue Contents--%n");

        int x = 0;
        for (Guest guest : queue) {
            System.out.format("%x: %s %s %n", x++, guest.toString(), x == 1 ? "(Head)" : "");
        }

        System.out.println("");

    }

}
