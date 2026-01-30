package com.iuh.fit.observer;

public interface Subject {
    void attach(Observer observer);
    void notifyObservers(String message);
}
