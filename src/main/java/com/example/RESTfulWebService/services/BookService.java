package com.example.RESTfulWebService.services;

import com.example.RESTfulWebService.controllers.book.BookRequestBody;
import com.example.RESTfulWebService.controllers.book.BookResponseBody;
import com.example.RESTfulWebService.persistence.BookEntity;
import com.example.RESTfulWebService.persistence.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Configurable
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<List<BookResponseBody>> getAllBooks() {
        Iterable<BookEntity> optionalBook = bookRepository.findAll();

        List<BookResponseBody> books = new ArrayList<>();
        optionalBook.forEach(b -> books.add(constructBookResponse(b)));
        if (books.isEmpty()) {
            throw new NoSuchElementException("No Books found");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Long>> countBooks(boolean isAvailable) {
        List<BookResponseBody> body = getAllBooks().getBody();
        long count;
        if (body != null) {
            count = body.parallelStream().filter(b -> isAvailable ^ b.getTakenByUserID() != null).count();
            if (isAvailable) {
                return new ResponseEntity<>(Collections.singletonMap("Available Books Count", count), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Collections.singletonMap("Borrowed Books Count", count), HttpStatus.OK);
            }
        }
        throw new NoSuchElementException("No Books found");
    }

    public ResponseEntity<BookResponseBody> getBookByID(String uuid) {
        Optional<BookEntity> optionalBook = bookRepository.findById(UUID.fromString(uuid));
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException("No Book with Id '" + uuid + "' found");
        }
        return new ResponseEntity<>(constructBookResponse(optionalBook.get()), HttpStatus.OK);
    }

    public ResponseEntity<List<BookResponseBody>> getBookByTitle(String title) {
        Optional<List<BookEntity>> optionalBooks = getBooksByTitle(title);
        return getBookList(optionalBooks);
    }

    public ResponseEntity<Map<String, Long>> countAvailableBooksByName(String title) {
        Optional<List<BookEntity>> optionalBooks = getBooksByTitle(title);
        if (optionalBooks.isEmpty() || optionalBooks.get().isEmpty()) {
            throw new NoSuchElementException("No Books with Name '" + title + "' found");
        }
        List<BookEntity> books = optionalBooks.get();
        long count;
        count = books.parallelStream().filter(b -> b.getTakenByUserId() == null).count();
        return new ResponseEntity<>(Collections.singletonMap("Available Books Count With Name '" + title + "'", count), HttpStatus.OK);
    }

    public ResponseEntity<List<BookResponseBody>> getBookByExactTitle(String title) {
        Optional<List<BookEntity>> optionalBooks = bookRepository.findByTitle(title);
        return getBookList(optionalBooks);
    }

    public ResponseEntity<BookResponseBody> getBookByISBN(String isbn) {
        Optional<BookEntity> optionalBook = bookRepository.findByISBN(isbn);
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException("No Book with ISBN '" + isbn + "' found");
        }
        return new ResponseEntity<>(constructBookResponse(optionalBook.get()), HttpStatus.OK);
    }

    public ResponseEntity<BookResponseBody> saveBook(BookRequestBody bookRequestBody) {
        BookEntity book = constructBook(bookRequestBody);
        BookEntity savedBook = bookRepository.save(book);
        return new ResponseEntity<>(constructBookResponse(savedBook), HttpStatus.OK);
    }

    public ResponseEntity<BookResponseBody> deleteBookById(String id) throws Exception {
        UUID uuid = UUID.fromString(id);
        Optional<BookEntity> optionalBook = bookRepository.findById(uuid);
        // did not find the book to delete
        if (optionalBook.isEmpty()) {
            throw new NoSuchElementException("No Book with Id '" + id + "' found for deletion");
        }
        // the book is borrowed
        if (optionalBook.get().getTakenByUserId() != null) {
            throw new IllegalArgumentException("Book with Id '" + id + "' is borrowed by User '" + optionalBook.get().getTakenByUserId() + "'");
        }
        BookEntity book = optionalBook.get();
        bookRepository.deleteById(uuid);

        optionalBook = bookRepository.findById(uuid);
        if (optionalBook.isEmpty()) {
            return new ResponseEntity<>(constructBookResponse(book), HttpStatus.OK);
        }
        // could not delete the book
        throw new Exception("Unable To delete Book with Id '" + id + "'");
    }

    private Optional<List<BookEntity>> getBooksByTitle(String title) {
        if (!title.startsWith("%")) {
            title = "%" + title;
        }
        if (!title.endsWith("%")) {
            title = title + "%";
        }
        return bookRepository.findByTitleLikeIgnoreCase(title);
    }

    public static BookResponseBody constructBookResponse(BookEntity book) {
        return new BookResponseBody(book.getId(), book.getTitle(), book.getISBN(), book.getCost(), book.getTakenByUserId());
    }

    public static BookEntity constructBook(BookRequestBody bookRequestBody) {
        UUID id = bookRequestBody.getUuid() == null ? UUID.randomUUID() : UUID.fromString(bookRequestBody.getUuid());
        return new BookEntity(id, bookRequestBody.getTitle(), bookRequestBody.getIsbn(), bookRequestBody.getCost(), null);
    }

    public static ResponseEntity<List<BookResponseBody>> getBookList(Optional<List<BookEntity>> optionalBook) {
        if (optionalBook.isEmpty() || optionalBook.get().isEmpty()) {
            throw new NoSuchElementException("No Books with such title found");
        }
        List<BookResponseBody> books = optionalBook
                .get()
                .parallelStream()
                .map(BookService::constructBookResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
