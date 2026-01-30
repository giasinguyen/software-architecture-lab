package factory;

public class BookFactory {
    public static Book createBook(String type, String title, String author, String category) {
        switch (type) {
            case "PAPER":
                return new PaperBook(title, author, category);
            case "EBOOK":
                return new EBook(title, author, category);
            case "AUDIO":
                return new AudioBook(title, author, category);
            default:
                throw new IllegalArgumentException("Loại sách không hợp lệ");
        }
    }
}
