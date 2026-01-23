package org.example.factorymethod;

public class Latte implements Coffee {
    @Override
    public void brew() {
        System.out.println("Pha Latte");
    }
}
