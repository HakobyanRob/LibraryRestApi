package com.example.RESTfulWebService.services;

import com.example.RESTfulWebService.controllers.book.BookResponseBody;
import com.example.RESTfulWebService.controllers.patron.PatronRequestBody;
import com.example.RESTfulWebService.controllers.patron.PatronResponseBody;
import com.example.RESTfulWebService.persistence.BookEntity;
import com.example.RESTfulWebService.persistence.BookRepository;
import com.example.RESTfulWebService.persistence.PatronEntity;
import com.example.RESTfulWebService.persistence.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Configurable
public class PatronService {

    private final PatronRepository patronRepository;
    private final BookRepository bookRepository;

    @Autowired
    public PatronService(PatronRepository patronRepository, BookRepository bookRepository) {
        this.patronRepository = patronRepository;
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<PatronResponseBody> getPatronById(UUID uuid) {
        Optional<PatronEntity> optionalPatron = patronRepository.findById(uuid);

        if (optionalPatron.isEmpty()) {
            throw new NoSuchElementException("No Patron with Id '" + uuid + "' found");
        }
        return new ResponseEntity<>(constructPatronResponse(optionalPatron.get()), HttpStatus.OK);
    }

    public ResponseEntity<PatronResponseBody> savePatron(PatronRequestBody patronRequestBody) {
        PatronEntity patron = constructPatron(patronRequestBody);
        PatronEntity savedPatron = patronRepository.save(patron);
        return new ResponseEntity<>(constructPatronResponse(savedPatron), HttpStatus.OK);
    }

    public ResponseEntity<BookResponseBody> borrowBook(String patron_uuid, String book_uuid) {
        Optional<BookEntity> optionalBook = bookRepository.findById(UUID.fromString(book_uuid));
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException("No Book with Id '" + book_uuid + "' found");
        }
        BookEntity book = optionalBook.get();
        if (book.getTakenByUserId() != null) {
            throw new IllegalArgumentException("The Book with Id '" + book_uuid + "' is already borrowed by User with Id '" + book.getTakenByUserId() + "'");
        } else {
            Optional<PatronEntity> optionalPatron = patronRepository.findById(UUID.fromString(patron_uuid));
            if (optionalPatron.isEmpty()) {
                throw new NoSuchElementException("No Patron with Id '" + patron_uuid + "' found");
            }
            book.setTakenByUserId(optionalPatron.get().getId());
            bookRepository.save(book);
        }
        return new ResponseEntity<>(BookService.constructBookResponse(book), HttpStatus.OK);
    }

    public ResponseEntity<BookResponseBody> returnBook(String patron_uuid, String book_uuid) {
        Optional<BookEntity> optionalBook = bookRepository.findById(UUID.fromString(book_uuid));
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException("No Book with Id '" + book_uuid + "' found");
        }
        BookEntity book = optionalBook.get();
        if (book.getTakenByUserId() == null) {
            throw new IllegalArgumentException("The Book with Id '" + book_uuid + "' is not borrowed");
        } else {
            Optional<PatronEntity> optionalPatron = patronRepository.findById(UUID.fromString(patron_uuid));
            if (optionalPatron.isEmpty()) {
                throw new NoSuchElementException("No Patron with Id '" + patron_uuid + "' found");
            }
            if (book.getTakenByUserId().equals(UUID.fromString(patron_uuid))) {
                book.setTakenByUserId(null);
            } else {
                throw new IllegalArgumentException("The Book with Id '" + book_uuid + "' is borrowed by Different User with Id '" + book.getTakenByUserId() + "'");
            }
            bookRepository.save(book);
        }
        return new ResponseEntity<>(BookService.constructBookResponse(book), HttpStatus.OK);
    }

    public ResponseEntity<PatronResponseBody> deletePatronById(UUID uuid) throws Exception {
        Optional<PatronEntity> optionalPatron = patronRepository.findById(uuid);
        // did not find the book to delete
        if (optionalPatron.isEmpty()) {
            throw new NoSuchElementException("No Patron with Id '" + uuid + "' found");
        }
        Long countBooks = countBooks(uuid);
        if (countBooks > 0) {
            throw new IllegalArgumentException("Unable to Delete Patron with Id '" + uuid + "'. They have " + countBooks + " books borrowed");
        }
        PatronEntity book = optionalPatron.get();
        patronRepository.deleteById(uuid);

        optionalPatron = patronRepository.findById(uuid);
        if (optionalPatron.isEmpty()) {
            return new ResponseEntity<>(constructPatronResponse(book), HttpStatus.OK);
        }
        // could not delete the book
        throw new RuntimeException("Unable To delete Book with Id '" + uuid + "'");
    }

    public ResponseEntity<Map<String, Long>> bookCountBorrowedByUser(UUID patronUUID) {
        ResponseEntity<PatronResponseBody> patronById = getPatronById(patronUUID);
        if (patronById.getBody() == null) {
            throw new NoSuchElementException("No Patron with Id '" + patronUUID + "' found");
        }
        Long count = countBooks(patronUUID);
        return new ResponseEntity<>(Collections.singletonMap("Books Borrowed by " + patronById.getBody().getName(), count), HttpStatus.OK);
    }

    public Long countBooks(UUID patronUUID) {
        long count = 0;

        for (BookEntity b : bookRepository.findAll()) {
            UUID takenByUserId = b.getTakenByUserId();
            if (takenByUserId != null && takenByUserId.equals(patronUUID)) {
                count++;
            }
        }
        return count;
    }

    public static PatronResponseBody constructPatronResponse(PatronEntity patronEntity) {
        return new PatronResponseBody(patronEntity.getId(), patronEntity.getName(), patronEntity.getDateOfBirth(), patronEntity.getPhoneNumber());
    }

    public static PatronEntity constructPatron(PatronRequestBody patronRequestBody) {
        UUID id = patronRequestBody.getUuid() == null ? UUID.randomUUID() : UUID.fromString(patronRequestBody.getUuid());
        return new PatronEntity(id, patronRequestBody.getName(), patronRequestBody.getDateOfBirth(), patronRequestBody.getPhoneNumber());
    }
}
