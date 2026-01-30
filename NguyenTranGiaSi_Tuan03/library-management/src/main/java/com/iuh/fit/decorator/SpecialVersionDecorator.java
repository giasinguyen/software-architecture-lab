package com.iuh.fit.decorator;

public class SpecialVersionDecorator extends BorrowDecorator {
    public SpecialVersionDecorator(Borrow borrow) {
        super(borrow);
    }

    @Override
    public String borrow() {
        return borrow.borrow() + " + Phiên bản đặc biệt";
    }
}
