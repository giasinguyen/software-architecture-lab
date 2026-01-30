package com.iuh.fit.factory;

public class EBook implements Book {
    private String title, author, category;

    public EBook(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }

    public String getInfo() {
        return "EBook: " + title;
    }
}