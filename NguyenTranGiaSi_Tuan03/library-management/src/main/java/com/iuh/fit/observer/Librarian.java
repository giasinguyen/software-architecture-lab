package com.iuh.fit.observer;

public class Librarian implements Observer {
    private String name;

    public Librarian(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println("[" + name + "] nhận thông báo: " + message);
    }
}
