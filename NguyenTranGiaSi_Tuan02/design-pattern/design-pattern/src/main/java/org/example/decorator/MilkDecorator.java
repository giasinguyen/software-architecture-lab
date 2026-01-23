package org.example.decorator;

public class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + Sữa";
    }

    @Override
    public int cost() {
        return coffee.cost() + 5000;
    }
}