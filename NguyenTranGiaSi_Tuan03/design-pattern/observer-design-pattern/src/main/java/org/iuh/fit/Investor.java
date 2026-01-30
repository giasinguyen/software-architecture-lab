package org.iuh.fit;

public class Investor implements Observer {
    private String name;

    public Investor(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " nhận thông báo: " + message);
    }
}

