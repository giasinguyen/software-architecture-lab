package org.example.factorymethod;

public class Espresso implements Coffee {
    @Override
    public void brew() {
        System.out.println("Pha Espresso");
    }
}
