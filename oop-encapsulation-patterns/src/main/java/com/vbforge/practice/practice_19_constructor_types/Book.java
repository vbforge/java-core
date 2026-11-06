package com.vbforge.practice.practice_19_constructor_types;

/**
 * Constructor kinds:
 *   1. no-arg (here delegating with this(...))
 *   2. parameterised
 *   3. copy constructor
 *   4. chaining with this(...), which must be the first statement
 *
 * Rule: once you declare ANY constructor, the compiler no longer adds the default one.
 */
public class Book {

    private final String title;
    private final String author;
    private final int    pages;
    private final String genre;

    public Book() {
        this("Unknown Title", "Unknown Author", 0, "General");
    }

    public Book(String title, String author) {
        this(title, author, 0, "General");
    }

    public Book(String title, String author, int pages, String genre) {
        this.title  = title;
        this.author = author;
        this.pages  = pages;
        this.genre  = genre;
    }

    public Book(Book other) {
        this(other.title, other.author, other.pages, other.genre);
    }

    @Override
    public String toString() {
        return "Book{\"" + title + "\" by " + author + ", " + pages + "p, " + genre + "}";
    }
}
