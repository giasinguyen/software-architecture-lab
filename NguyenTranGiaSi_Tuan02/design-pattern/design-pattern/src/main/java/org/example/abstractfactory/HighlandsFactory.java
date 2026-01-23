package org.example.abstractfactory;

public class HighlandsFactory implements CoffeeShopFactory {
    @Override
    public Coffee createEspresso() {
        return () -> System.out.println("Highlands pha Espresso");
    }

    @Override
    public Coffee createLatte() {
        return () -> System.out.println("Highlands pha Latte");
    }
}
