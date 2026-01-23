package org.example.factorymethod;

public class FactoryMethodDemo {
    public static void main(String[] args) {
        Coffee coffee = CoffeeFactory.createCoffee("LATTE");
        coffee.brew();
    }
}
