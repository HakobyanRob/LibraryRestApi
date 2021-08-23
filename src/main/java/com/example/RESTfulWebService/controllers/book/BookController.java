package com.example.RESTfulWebService.controllers.book;

import com.example.RESTfulWebService.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Configurable
@RestController
public class BookController {

    private final BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/book")
    public ResponseEntity<List<BookResponseBody>> getAllBooks() {
        return service.getAllBooks();
    }

    @GetMapping("/countBooks")
    public ResponseEntity<Map<String, Long>> countBooks(@RequestParam(value = "isAvailable", required = false, defaultValue = "true") boolean isAvailable) {
        return service.countBooks(isAvailable);
    }

    @GetMapping("/countBooksByName/{title}")
    public ResponseEntity<Map<String, Long>> getAllAvailableBooksByName(@PathVariable String title) {
        if (title == null) {
            throw new IllegalArgumentException("Book's Title may not be null");
        }
        return service.countAvailableBooksByName(title);
    }

    @GetMapping("/book/{uuid}")
    public ResponseEntity<BookResponseBody> getBookByID(@PathVariable String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Book's ID may not be null");
        }
        return service.getBookByID(uuid);
    }

    @GetMapping("/bookByExactTitle/{title}")
    public ResponseEntity<List<BookResponseBody>> getBookByExactTitle(@PathVariable String title) {
        if (title == null) {
            throw new IllegalArgumentException("Book's Title may not be null");
        }
        return service.getBookByExactTitle(title);
    }

    @GetMapping("/bookByTitle/{title}")
    public ResponseEntity<List<BookResponseBody>> getBookByTitle(@PathVariable String title) {
        if (title == null) {
            throw new IllegalArgumentException("Book's Title may not be null");
        }
        return service.getBookByTitle(title);
    }

    @GetMapping("/bookByISBN/{isbn}")
    public ResponseEntity<BookResponseBody> getBookByISBN(@PathVariable String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("Book's ISBN may not be null");
        }
        return service.getBookByISBN(isbn);
    }

    @PostMapping(value = "/book", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookResponseBody> addBook(@Valid @RequestBody BookRequestBody bookRequestBody) {
        if (bookRequestBody.getUuid() != null) {
            throw new IllegalArgumentException("New Book's ID must be null");
        }
        return service.saveBook(bookRequestBody);
    }

    @PatchMapping(value = "/book", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookResponseBody> updateBook(@Valid @RequestBody BookRequestBody bookRequestBody) {
        return service.saveBook(bookRequestBody);
    }

    @DeleteMapping("/bookById/{uuid}")
    public ResponseEntity<BookResponseBody> deleteBookById(@PathVariable String uuid) throws Exception {
        if (uuid == null) {
            throw new IllegalArgumentException("Book's ID may not be null");
        }
        return service.deleteBookById(uuid);
    }
}