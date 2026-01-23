package org.example.factorymethod;

public class CoffeeFactory {
    public static Coffee createCoffee(String type) {
        if (type.equalsIgnoreCase("ESPRESSO")) {
            return new Espresso();
        } else if (type.equalsIgnoreCase("LATTE")) {
            return new Latte();
        }
        throw new IllegalArgumentException("Không có loại cà phê này");
    }
}
