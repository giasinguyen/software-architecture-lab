package org.example.abstractfactory;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        CoffeeShopFactory factory = new StarbucksFactory();
        Coffee coffee = factory.createLatte();
        coffee.brew();
    }
}
