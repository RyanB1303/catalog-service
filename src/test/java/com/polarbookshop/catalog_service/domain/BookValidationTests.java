package com.polarbookshop.catalog_service.domain;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSuccess() {
        var book = Book.of("1234567890", "Title", "Author", 9.99, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnDefinedButNotCorrectThenValidationFails() {
        var book = Book.of("123", "Title", "Author", 9.99, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @Test
    void whenIsbnNotDefinedThenValidationFails() {
        var book = Book.of("", "Title", "Author", 9.99, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(2);
        List<String> constraintViolationMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        assertThat(constraintViolationMessages)
                .contains("The book ISBN must be defined.")
                .contains("The ISBN format must be valid.");
    }

    @Test
    void whenTitleNotDefinedThenValidationFails() {
        var book = Book.of("1234567890", "", "Author", 9.99, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorNotDefinedThenValidationFails() {
        var book = Book.of("1234567890", "Title", "", 9.99, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");
    }

    @Test
    void whenPriceNotDefinedThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", null, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenPriceDefinedButNotPositiveThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", -10.0, "Polar");
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

}
