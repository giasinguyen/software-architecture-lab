package org.example.decorator;


public class BasicCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Cà phê đen";
    }

    @Override
    public int cost() {
        return 20000;
    }
}
