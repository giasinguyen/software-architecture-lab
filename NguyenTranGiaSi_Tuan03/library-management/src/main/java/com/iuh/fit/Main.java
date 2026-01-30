
import decorator.*;
import factory.Book;
import factory.BookFactory;
import observer.Librarian;
import singleton.Library;
import strategy.SearchByTitle;

public class Main {
    public static void main(String[] args) {

        Library library = Library.getInstance();

        library.attach(new Librarian("Thủ thư A"));

        Book b1 = BookFactory.createBook("PAPER", "Java", "James", "IT");
        Book b2 = BookFactory.createBook("EBOOK", "Design Pattern", "GoF", "IT");

        library.addBook(b1);
        library.addBook(b2);

        System.out.println("=== Tìm sách theo tên ===");
        new SearchByTitle()
                .search(library.getBooks(), "Java")
                .forEach(b -> System.out.println(b.getInfo()));

        System.out.println("=== Mượn sách nâng cao ===");
        Borrow borrow = new ExtendTimeDecorator(
                new SpecialVersionDecorator(
                        new BasicBorrow()));
        System.out.println(borrow.borrow());
    }
}
