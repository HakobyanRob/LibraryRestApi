package com.example.RESTfulWebService.controllers.patron;

import com.example.RESTfulWebService.controllers.book.BookResponseBody;
import com.example.RESTfulWebService.services.PatronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Configurable
@RestController
public class PatronController {

    private final PatronService service;

    @Autowired
    public PatronController(PatronService service) {
        this.service = service;
    }

    @GetMapping("/patron/{uuid}")
    public ResponseEntity<PatronResponseBody> getPatronByID(@PathVariable String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Patron's ID may not be null");
        }
        return service.getPatronById(UUID.fromString(uuid));
    }

    @GetMapping("/booksBorrowedBy/{uuid}")
    public ResponseEntity<Map<String, Long>> getBookCountBorrowedByUser(@PathVariable String uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("Patron's ID may not be null");
        }
        return service.bookCountBorrowedByUser(UUID.fromString(uuid));
    }

    @PostMapping(value = "/patron", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatronResponseBody> addPatron(@Valid @RequestBody PatronRequestBody patronRequestBody) {
        if (patronRequestBody.getUuid() != null) {
            throw new IllegalArgumentException("New Patron's ID must be null");
        }
        return service.savePatron(patronRequestBody);
    }

    @PatchMapping(value = "/patron", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PatronResponseBody> updatePatron(@Valid @RequestBody PatronRequestBody patronRequestBody) {
        return service.savePatron(patronRequestBody);
    }

    @PostMapping(value = "/patron/{patron_uuid}/borrow/{book_uuid}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookResponseBody> borrowBook(@PathVariable String patron_uuid, @PathVariable String book_uuid) {
        if (patron_uuid == null) {
            throw new IllegalArgumentException("Patron's ID may not be null");
        }
        if (book_uuid == null) {
            throw new IllegalArgumentException("Book's ID may not be null");
        }
        return service.borrowBook(patron_uuid, book_uuid);
    }

    @PostMapping(value = "/patron/{patron_uuid}/return/{book_uuid}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookResponseBody> returnBook(@PathVariable String patron_uuid, @PathVariable String book_uuid) {
        if (patron_uuid == null) {
            throw new IllegalArgumentException("Patron's ID may not be null");
        }
        if (book_uuid == null) {
            throw new IllegalArgumentException("Book's ID may not be null");
        }
        return service.returnBook(patron_uuid, book_uuid);
    }

    @DeleteMapping("/patron/{uuid}")
    public ResponseEntity<PatronResponseBody> deletePatronById(@PathVariable String uuid) throws Exception {
        if (uuid == null) {
            throw new IllegalArgumentException("Patron's ID may not be null");
        }
        return service.deletePatronById(UUID.fromString(uuid));
    }
}
