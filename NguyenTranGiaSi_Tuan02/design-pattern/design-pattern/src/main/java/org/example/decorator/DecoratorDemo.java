package org.example.decorator;

public class DecoratorDemo {
    public static void main(String[] args) {
        Coffee coffee = new BasicCoffee();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);

        System.out.println(coffee.getDescription());
        System.out.println("Giá: " + coffee.cost());
    }
}
