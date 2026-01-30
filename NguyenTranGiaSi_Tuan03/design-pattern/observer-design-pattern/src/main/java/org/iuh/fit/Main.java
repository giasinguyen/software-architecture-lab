package org.iuh.fit;

public class Main {
    public static void main(String[] args) {

        Stock stock = new Stock();

        Observer investor1 = new Investor("Nguyễn Văn A");
        Observer investor2 = new Investor("Trần Thị B");
        Observer investor3 = new Investor("Lê Văn C");

        stock.attach(investor1);
        stock.attach(investor2);
        stock.attach(investor3);

        stock.setPrice(120.5);
        System.out.println("----");
        stock.setPrice(130.0);

        stock.detach(investor2);
        System.out.println("----");

        stock.setPrice(140.75);
    }
}
