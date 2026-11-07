package com.vbforge.practice.queue.practice_42_priority_queue_comparators;

import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityQueueBookComparable {
    public static void main(String[] args) {

        //creating content
        Book b1 = new Book(15, "Spain", "John", "EU", 20);
        Book b2 = new Book(32, "Geman", "Dereck", "EU", 12);
        Book b3 = new Book(27, "United States", "Garry", "NA", 27);
        Book b4 = new Book(755, "Brazil", "Dung", "SA", 20);
        Book b5 = new Book(68, "India", "Draws", "EA", 20);

        //initialize the queue and add content into
        Queue<Book> queue = new PriorityQueue<>();
        queue.add(b4);
        queue.add(b2);
        queue.add(b1);
        queue.add(b5);
        queue.add(b3);

        //iterating through queue (for-each) shows the HEAP order, not sorted order;
        //only the head (smallest id) is guaranteed to be first
        System.out.println("Initial queue:");
        for (Book book: queue) {
            System.out.println(book);
        }

        //removing the head of this queue
        Book removed1 = queue.remove();
        System.out.println("Remove head #1: " + removed1);

        System.out.println("Queue after remove 1: ");
        for (Book book: queue) {
            System.out.println(book);
        }

        //remove again the head of this queue
        Book removed2 = queue.remove();
        System.out.println("Remove head #2: " + removed2);


        System.out.println("Queue after remove 2: ");
        for (Book book: queue) {
            System.out.println(book);
        }

        //remove again the head of this queue
        Book removed3 = queue.remove();
        System.out.println("Remove head #3: " + removed3);


        System.out.println("Queue after remove 3: ");
        for (Book book: queue) {
            System.out.println(book);
        }

    }

    //model performed as comparable (priority-based by id of the book)
    static class Book implements Comparable<Book> {

        private int id;
        private String name;
        private String author;
        private String publisher;
        private int quantity;

        public Book() {
        }

        public Book(int id, String name, String author, String publisher, int quantity) {
            this.id = id;
            this.name = name;
            this.author = author;
            this.publisher = publisher;
            this.quantity = quantity;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Book book = (Book) object;
            return id == book.id &&
                    quantity == book.quantity &&
                    Objects.equals(name, book.name) &&
                    Objects.equals(author, book.author) &&
                    Objects.equals(publisher, book.publisher);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, author, publisher, quantity);
        }

        @Override
        public String toString() {
            return "Book{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", author='" + author + '\'' +
                    ", publisher='" + publisher + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }

        @Override
        public int compareTo(Book o) {
            return Integer.compare(this.id, o.id); //for priority of head of queue
        }
    }

}
