package com.iuh.fit.decorator;

public class BasicBorrow implements Borrow {
    @Override
    public String borrow() {
        return "Mượn sách cơ bản";
    }
}
