package strategy;

import factory.Book;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByCategory implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String keyword) {
        return books.stream()
                .filter(b -> b.getCategory().contains(keyword))
                .collect(Collectors.toList());
    }
}

