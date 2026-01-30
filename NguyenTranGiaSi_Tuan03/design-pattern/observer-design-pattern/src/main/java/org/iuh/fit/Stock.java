package org.iuh.fit;

import java.util.ArrayList;
import java.util.List;

public class Stock implements Subject {
    private double price;
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update("Giá cổ phiếu thay đổi: " + price);
        }
    }

    public void setPrice(double price) {
        this.price = price;
        notifyObservers();
    }
}
