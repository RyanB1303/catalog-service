package com.polarbookshop.catalog_service.domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.polarbookshop.catalog_service.config.DataConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc
        .AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;


@DataJdbcTest
@Import(DataConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
public class BookRepositoryJdbcTests {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private JdbcAggregateTemplate jdbcAggregateTemplate;

    @Test
    void findAllBooks() {
        var book1 = Book.of("1234567883", "TitleA", "AuthorA", 1.33, "Polar");
        var book2 = Book.of("1234567894", "TitleB", "AuthorB", 2.33, "Polar");
        jdbcAggregateTemplate.insert(book1);
        jdbcAggregateTemplate.insert(book2);

        Iterable<Book> actualBooks = bookRepository.findAll();

        assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
                .filter(book -> book.isbn().equals(book1.isbn()) || book.isbn().equals(book2.isbn()))
                .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234567897";
        var book = Book.of(bookIsbn, "Title", "author", 6.9, "Polar");
        jdbcAggregateTemplate.insert(book);
        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().isbn()).isEqualTo(book.isbn());
    }

    @Test
    void findBookByIsbnWhenNotExisting() {
        boolean existing = bookRepository.existsByIsbn("1234567898");
        assertThat(existing).isFalse();
    }

    @Test
    void deleteByIsbn() {
        var bookIsbn = "1234567893";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 3.9, "Polar");
        var persistedBook = jdbcAggregateTemplate.insert(bookToCreate);

        bookRepository.deleteByIsbn(bookIsbn);
        assertThat(jdbcAggregateTemplate.findById(persistedBook.id(), Book.class)).isNull();
    }
}
