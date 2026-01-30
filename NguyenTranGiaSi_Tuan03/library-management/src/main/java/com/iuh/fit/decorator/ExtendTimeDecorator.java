package decorator;

public class ExtendTimeDecorator extends BorrowDecorator {
    public ExtendTimeDecorator(Borrow borrow) {
        super(borrow);
    }

    @Override
    public String borrow() {
        return borrow.borrow() + " + Gia hạn thời gian";
    }
}
