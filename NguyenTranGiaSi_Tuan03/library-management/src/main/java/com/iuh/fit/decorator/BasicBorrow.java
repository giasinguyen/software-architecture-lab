package decorator;

public class BasicBorrow implements Borrow {
    @Override
    public String borrow() {
        return "Mượn sách cơ bản";
    }
}
