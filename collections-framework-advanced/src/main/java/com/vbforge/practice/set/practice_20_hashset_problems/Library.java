package com.vbforge.practice.set.practice_20_hashset_problems;

import java.util.*;

public class Library {

    //type of collection:
    private final Set<Book> books; // Using Set to ignore duplicates

    //constructor:
    public Library() {
        this.books = new HashSet<>(); // Initialize with HashSet to avoid duplicates
    }

    //method to add book in collection:
    public void addBook(Book book){
        books.add(book);
    }

    //method for printing sorted books:
    public void printSortedBooksByYear(){
        List<Book> listBooks = new ArrayList<>(books); //convert into a Collection as List (so we can sort)
        Collections.sort(listBooks); //sort by publication year
        int count = 1;
        for (Book book : listBooks) {
            System.out.println(count + ") " + book);
            count++;
        }
    }

    //method if we need to find book by title:
    public Book findBookByTitle(String title){
        for (Book book : books) {
            if(book.getTitle().equals(title)){
                return book;
            }
        }
        return null;
    }


    //=====clients part:=====
    public static void main(String[] args) {

        //create our library:
        Library library = new Library();

        //create content and test add method:
        library.addBook(new Book("Sponge Bob", "Steven Ellensburg", 2011));
        library.addBook(new Book("Bob with Square Pants", "Sweeten and Dad", 2024));
        library.addBook(new Book("Sponge Bob adventures", "Steven Ellensburg", 2010));
        library.addBook(new Book("Sponge Bob", "Steven Ellensburg", 2011)); // Duplicate, won't be added

        //sorting method testing:
        System.out.println("Books sorted by publication year:");
        library.printSortedBooksByYear();

        //find book by title test:
        System.out.println("---------");
        Book bookByTitle = library.findBookByTitle("Bob with Square Pants");
        if(bookByTitle != null){
            System.out.println("Found: " + bookByTitle);
        } else {
            System.out.println("book is not found");
        }

    }
}
