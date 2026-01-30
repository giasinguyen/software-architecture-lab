package com.iuh.fit.singleton;

import com.iuh.fit.factory.Book;
import com.iuh.fit.observer.Observer;
import com.iuh.fit.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class Library implements Subject {
    private static Library instance;
    private List<Book> books = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();

    private Library() {}

    public static Library getInstance() {
        if (instance == null) {
            instance = new Library();
        }
        return instance;
    }

    public void addBook(Book book) {
        books.add(book);
        notifyObservers("Thư viện có sách mới: " + book.getInfo());
    }

    public List<Book> getBooks() {
        return books;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) {
            o.update(message);
        }
    }
}
