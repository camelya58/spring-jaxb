package com.github.camelya58.controller;

import com.github.camelya58.model.Book;
import com.github.camelya58.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class BookController is a REST-controller for creating books by templates.
 *
 * @author Kamila Meshcheryakova
 * created 07.09.2020
 */
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/javaObject")
    public Book createBookFromXml() {
        return bookService.createBook();
    }

    @GetMapping("/xml")
    public String createXml() {
        bookService.createXml();
        return "Xml file has created";
    }
}
