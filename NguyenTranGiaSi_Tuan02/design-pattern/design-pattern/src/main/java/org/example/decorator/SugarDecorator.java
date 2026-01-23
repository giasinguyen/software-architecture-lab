package org.example.decorator;

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + " + Đường";
    }

    @Override
    public int cost() {
        return coffee.cost() + 2000;
    }
}
