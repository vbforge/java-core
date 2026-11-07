package com.vbforge.practice.set.practice_20_hashset_problems;

public class Book implements Comparable<Book>{

    private final String title;
    private final String author;
    private final int publicationYear;

    public Book(String title, String author, int publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    @Override
    public int compareTo(Book o) {
        return Integer.compare(this.publicationYear, o.publicationYear);
    }

    //basic:
    /*@Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Book book = (Book) object;
        return publicationYear == book.publicationYear && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publicationYear);
    }*/

    //other version:
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book other = (Book) obj;
        return title.equals(other.title) && author.equals(other.author);
    }

    @Override
    public int hashCode() {
        return 31 * title.hashCode() + author.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s by %s (%d)", title, author, publicationYear);
    }
}
