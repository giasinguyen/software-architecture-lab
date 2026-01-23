package org.example.abstractfactory;

public class StarbucksFactory implements CoffeeShopFactory {
    @Override
    public Coffee createEspresso() {
        return () -> System.out.println("Starbucks pha Espresso");
    }

    @Override
    public Coffee createLatte() {
        return () -> System.out.println("Starbucks pha Latte");
    }
}