package com.iuh.fit.strategy;

import com.iuh.fit.factory.Book;
import java.util.List;

public interface SearchStrategy {
    List<Book> search(List<Book> books, String keyword);
}
