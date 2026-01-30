package com.iuh.fit.factory;

public class PaperBook implements Book {
    private String title, author, category;

    public PaperBook(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }

    public String getInfo() {
        return "PaperBook: " + title;
    }
}
