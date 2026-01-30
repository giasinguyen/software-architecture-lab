package com.iuh.fit.decorator;

public abstract class BorrowDecorator implements Borrow {
    protected Borrow borrow;

    public BorrowDecorator(Borrow borrow) {
        this.borrow = borrow;
    }
}
