package com.example.RESTfulWebService;

import com.example.RESTfulWebService.controllers.book.BookRequestBody;
import com.example.RESTfulWebService.controllers.book.BookResponseBody;
import com.example.RESTfulWebService.controllers.patron.PatronRequestBody;
import com.example.RESTfulWebService.controllers.patron.PatronResponseBody;
import com.example.RESTfulWebService.persistence.BookEntity;
import com.example.RESTfulWebService.persistence.BookRepository;
import com.example.RESTfulWebService.persistence.PatronEntity;
import com.example.RESTfulWebService.persistence.PatronRepository;
import com.example.RESTfulWebService.services.BookService;
import com.example.RESTfulWebService.services.PatronService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest
class RESTfulWebServiceApplicationTests {

    private final BookService bookService;
    private final PatronService patronService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private PatronRepository patronRepository;

    @Autowired
    public RESTfulWebServiceApplicationTests(BookService bookService, PatronService patronService) {
        this.bookService = bookService;
        this.patronService = patronService;
    }

    @Test
    public void getBookByIDTest() {
        UUID id = UUID.randomUUID();
        BookEntity mockBook = new BookEntity(id, "Harry Potter", "132456", 20.0, null);
        when(bookRepository.findById(id)).thenReturn(Optional.of(mockBook));
        ResponseEntity<BookResponseBody> bookByID = bookService.getBookByID(id.toString());
        Assertions.assertNotNull(bookByID);
        Assertions.assertNotNull(bookByID.getBody());
        Assertions.assertEquals(bookByID.getBody().getId(), id);
        Assertions.assertEquals(bookByID.getBody().getTitle(), "Harry Potter");
        Assertions.assertEquals(bookByID.getBody().getISBN(), "132456");
        Assertions.assertEquals(bookByID.getBody().getCost(), 20.0);
    }

    @Test
    public void getBookByISBNTest() {
        UUID id = UUID.randomUUID();
        String isbn = "132456";
        BookEntity mockBook = new BookEntity(id, "Harry Potter", isbn, 20.0, null);
        when(bookRepository.findByISBN(isbn)).thenReturn(Optional.of(mockBook));
        ResponseEntity<BookResponseBody> bookByISBN = bookService.getBookByISBN(isbn);
        Assertions.assertNotNull(bookByISBN);
        Assertions.assertNotNull(bookByISBN.getBody());
        Assertions.assertEquals(bookByISBN.getBody().getId(), id);
        Assertions.assertEquals(bookByISBN.getBody().getTitle(), "Harry Potter");
        Assertions.assertEquals(bookByISBN.getBody().getISBN(), isbn);
        Assertions.assertEquals(bookByISBN.getBody().getCost(), 20.0);
    }

    @Test
    public void getBookByExactTitleTest() {
        UUID id = UUID.randomUUID();
        String title = "Harry Potter";
        List<BookEntity> mockBook = Collections.singletonList(new BookEntity(id, title, "132456", 20.0, null));
        when(bookRepository.findByTitle(title)).thenReturn(Optional.of(mockBook));
        ResponseEntity<List<BookResponseBody>> booksByExactTitle = bookService.getBookByExactTitle(title);
        Assertions.assertNotNull(booksByExactTitle);
        List<BookResponseBody> books = booksByExactTitle.getBody();
        Assertions.assertNotNull(books);
        BookResponseBody book = books.get(0);
        Assertions.assertEquals(book.getId(), id);
        Assertions.assertEquals(book.getTitle(), title);
        Assertions.assertEquals(book.getISBN(), "132456");
        Assertions.assertEquals(book.getCost(), 20.0);
    }

    @Test
    public void addBookTest() {
        UUID id = UUID.randomUUID();
        BookRequestBody mockCreateBook = new BookRequestBody(null, "Harry Potter", "132456", 20.0);
        BookEntity mockReturnBook = new BookEntity(id, "Harry Potter", "132456", 20.0, null);
        when(bookRepository.save(any(BookEntity.class))).thenReturn(mockReturnBook);
        Assertions.assertNotNull(bookService.saveBook(mockCreateBook));
        BookResponseBody body = bookService.saveBook(mockCreateBook).getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(body.getId(), id);
        Assertions.assertEquals(body.getTitle(), mockCreateBook.getTitle());
        Assertions.assertEquals(body.getISBN(), mockCreateBook.getIsbn());
        Assertions.assertEquals(body.getCost(), mockCreateBook.getCost());
    }

    @Test
    public void countAvailableBooksByNameTest() {
        String title = "Harry";
        BookEntity book1 = new BookEntity(UUID.randomUUID(), "Harry Potter", "132456", 20.0, null);
        BookEntity book2 = new BookEntity(UUID.randomUUID(), "Harry Potter 3", "132457", 50.0, UUID.randomUUID());
        BookEntity book3 = new BookEntity(UUID.randomUUID(), "Harry Potter 2", "789724", 12.4, null);
        List<BookEntity> books = Arrays.asList(book1, book2, book3);
        when(bookRepository.findByTitleLikeIgnoreCase("%" + title + "%")).thenReturn(Optional.of(books));
        ResponseEntity<Map<String, Long>> mapResponseEntity = bookService.countAvailableBooksByName(title);
        Assertions.assertNotNull(mapResponseEntity.getBody());
        Assertions.assertEquals(1, mapResponseEntity.getBody().size());
        for (long count : mapResponseEntity.getBody().values()) {
            Assertions.assertEquals(2, count);
        }
    }

    @Test
    public void countBooksByNameTest() {
        String title = "Harry";
        BookEntity book1 = new BookEntity(UUID.randomUUID(), "Harry Potter", "132456", 20.0, null);
        BookEntity book2 = new BookEntity(UUID.randomUUID(), "Harry Potter 3", "132457", 50.0, UUID.randomUUID());
        BookEntity book3 = new BookEntity(UUID.randomUUID(), "Harry Potter 2", "789724", 12.4, null);
        List<BookEntity> bookEntities = Arrays.asList(book1, book2, book3);
        when(bookRepository.findByTitleLikeIgnoreCase("%" + title + "%")).thenReturn(Optional.of(bookEntities));
        ResponseEntity<List<BookResponseBody>> books = bookService.getBookByTitle(title);
        Assertions.assertNotNull(books.getBody());
        Assertions.assertFalse(books.getBody().isEmpty());
        Assertions.assertEquals(3, books.getBody().size());
        books.getBody().forEach(b -> Assertions.assertTrue(b.getTitle().contains(title)));
    }

    @Test
    public void countBorrowedBooksTest() {
        BookEntity book1 = new BookEntity(UUID.randomUUID(), "Harry Potter", "132456", 20.0, UUID.randomUUID());
        BookEntity book2 = new BookEntity(UUID.randomUUID(), "Harry Potter 3", "132457", 50.0, UUID.randomUUID());
        BookEntity book3 = new BookEntity(UUID.randomUUID(), "White Fang", "987987", 12.4, UUID.randomUUID());
        BookEntity book4 = new BookEntity(UUID.randomUUID(), "Harry Potter 2", "789724", 12.4, null);
        List<BookEntity> books = Arrays.asList(book1, book2, book3, book4);
        when(bookRepository.findAll()).thenReturn(books);
        ResponseEntity<Map<String, Long>> mapResponseEntity = bookService.countBooks(false);
        Assertions.assertNotNull(mapResponseEntity.getBody());
        Assertions.assertEquals(1, mapResponseEntity.getBody().size());
        for (long count : mapResponseEntity.getBody().values()) {
            Assertions.assertEquals(3, count);
        }
    }

    @Test
    public void deleteBookByIdTest() throws Exception {
        UUID id = UUID.randomUUID();
        BookEntity book = new BookEntity(id, "Harry Potter", "132456", 20.0, null);
        when(bookRepository.findById(id)).thenReturn(Optional.of(book)).thenReturn(Optional.empty());
        ResponseEntity<BookResponseBody> bookResponse = bookService.deleteBookById(id.toString());
        Assertions.assertNotNull(bookResponse);
        Mockito.verify(bookRepository, times(2)).findById(id);
        Mockito.verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    public void getPatronByIDTest() {
        UUID id = UUID.randomUUID();
        PatronEntity mockReturnPatron = new PatronEntity(id, "Michael", "12-MAR-1980", "015-45-45-78");
        when(patronRepository.findById(id)).thenReturn(Optional.of(mockReturnPatron));
        ResponseEntity<PatronResponseBody> patronById = patronService.getPatronById(id);
        Assertions.assertNotNull(patronById);
        Assertions.assertNotNull(patronById.getBody());
        Assertions.assertEquals(patronById.getBody().getUuid(), id);
        Assertions.assertEquals(patronById.getBody().getName(), "Michael");
        Assertions.assertEquals(patronById.getBody().getPhoneNumber(), "015-45-45-78");
        Assertions.assertEquals(patronById.getBody().getDateOfBirth(), "12-MAR-1980");
    }

    @Test
    public void savePatronTest() {
        UUID id = UUID.randomUUID();
        PatronRequestBody mockCreatePatron = new PatronRequestBody(null, "Michael", "12-MAR-1980", "015-45-45-78");
        PatronEntity mockReturnPatron = new PatronEntity(id, "Michael", "12-MAR-1980", "015-45-45-78");
        when(patronRepository.save(any(PatronEntity.class))).thenReturn(mockReturnPatron);
        Assertions.assertNotNull(patronService.savePatron(mockCreatePatron));
        PatronResponseBody patron = patronService.savePatron(mockCreatePatron).getBody();
        Assertions.assertNotNull(patron);
        Assertions.assertEquals(patron.getUuid(), id);
        Assertions.assertEquals(patron.getName(), mockCreatePatron.getName());
        Assertions.assertEquals(patron.getDateOfBirth(), mockCreatePatron.getDateOfBirth());
        Assertions.assertEquals(patron.getPhoneNumber(), mockCreatePatron.getPhoneNumber());
    }

    @Test
    public void borrowBookTest() {
        UUID patronID = UUID.randomUUID();
        UUID bookID = UUID.randomUUID();
        BookEntity mockReturnGetBook = new BookEntity(bookID, "White Fang", "222222", 12.4, null);
        BookEntity mockReturnSaveBook = new BookEntity(bookID, "White Fang", "222222", 12.4, patronID);
        PatronEntity mockReturnPatron = new PatronEntity(patronID, "Michael", "12-MAR-1980", "015-45-45-78");
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(mockReturnGetBook));
        when(patronRepository.findById(patronID)).thenReturn(Optional.of(mockReturnPatron));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(mockReturnSaveBook);
        ResponseEntity<BookResponseBody> bookResponse = patronService.borrowBook(patronID.toString(), bookID.toString());
        Assertions.assertNotNull(bookResponse.getBody());
        Assertions.assertEquals(bookResponse.getBody().getTakenByUserID(), patronID);
        Assertions.assertEquals(bookResponse.getBody().getId(), bookID);
    }

    @Test
    public void returnBookTest() {
        UUID patronID = UUID.randomUUID();
        UUID bookID = UUID.randomUUID();
        BookEntity mockReturnGetBook = new BookEntity(bookID, "White Fang", "222222", 12.4, patronID);
        BookEntity mockReturnSaveBook = new BookEntity(bookID, "White Fang", "222222", 12.4, null);
        PatronEntity mockReturnPatron = new PatronEntity(patronID, "Michael", "12-MAR-1980", "015-45-45-78");
        when(bookRepository.findById(bookID)).thenReturn(Optional.of(mockReturnGetBook));
        when(patronRepository.findById(patronID)).thenReturn(Optional.of(mockReturnPatron));
        when(bookRepository.save(any(BookEntity.class))).thenReturn(mockReturnSaveBook);
        ResponseEntity<BookResponseBody> bookResponse = patronService.returnBook(patronID.toString(), bookID.toString());
        Assertions.assertNotNull(bookResponse.getBody());
        Assertions.assertNull(bookResponse.getBody().getTakenByUserID());
        Assertions.assertEquals(bookResponse.getBody().getId(), bookID);
    }

    @Test
    public void bookCountBorrowedByUserTest() {
        UUID patronID = UUID.randomUUID();
        BookEntity book1 = new BookEntity(UUID.randomUUID(), "Harry Potter", "132456", 20.0, patronID);
        BookEntity book2 = new BookEntity(UUID.randomUUID(), "White Fang", "1234987", 30.0, patronID);
        BookEntity book3 = new BookEntity(UUID.randomUUID(), "Lost Ember", "789724", 12.4, null);
        List<BookEntity> books = Arrays.asList(book1, book2, book3);
        PatronEntity mockReturnPatron = new PatronEntity(patronID, "Michael", "12-MAR-1980", "015-45-45-78");
        when(bookRepository.findAll()).thenReturn(books);
        when(patronRepository.findById(patronID)).thenReturn(Optional.of(mockReturnPatron));
        ResponseEntity<Map<String, Long>> mapResponseEntity = patronService.bookCountBorrowedByUser(patronID);
        Assertions.assertNotNull(mapResponseEntity.getBody());
        Assertions.assertEquals(1, mapResponseEntity.getBody().size());
        for (long count : mapResponseEntity.getBody().values()) {
            Assertions.assertEquals(2, count);
        }
    }

    @Test
    public void deletePatronByIdTest() throws Exception {
        UUID id = UUID.randomUUID();
        PatronEntity patron = new PatronEntity(id, "John Smith", "25-FEB-1998", "097-789-565");
        when(patronRepository.findById(id)).thenReturn(Optional.of(patron)).thenReturn(Optional.empty());
        ResponseEntity<PatronResponseBody> patronResponse = patronService.deletePatronById(id);
        Assertions.assertNotNull(patronResponse);
        Mockito.verify(patronRepository, times(2)).findById(id);
        Mockito.verify(patronRepository, times(1)).deleteById(id);
    }

	/*@Test
	public void addBook() {
		//add Book
		BookRequestBody bookReqBody = new BookRequestBody(null, harry_potter, isbn, cost);
		ResponseEntity<BookResponseBody> bookResponse = bookService.saveBook(bookReqBody);
		Assertions.assertNotNull(bookResponse);
		BookResponseBody book = bookResponse.getBody();
		Assertions.assertNotNull(book);
		Assertions.assertEquals(book.getTitle(), harry_potter);
		Assertions.assertEquals(book.getISBN(), isbn);
		Assertions.assertEquals(book.getCost(), cost);
		Assertions.assertNull(book.getTakenByUserID());
		uuid = book.getId().toString();

		//get Book by ID
		bookResponse = bookService.getBookByID(uuid);
		Assertions.assertNotNull(bookResponse);
		book = bookResponse.getBody();
		Assertions.assertNotNull(book);
		Assertions.assertEquals(book.getId().toString(), uuid);
		Assertions.assertEquals(book.getTitle(), harry_potter);
		Assertions.assertEquals(book.getISBN(), isbn);
		Assertions.assertEquals(book.getCost(), cost);
		Assertions.assertNull(book.getTakenByUserID());

		//get Book by Exact Title
		ResponseEntity<List<BookResponseBody>> bookListResponse = bookService.getBookByExactTitle(harry_potter);
		Assertions.assertNotNull(bookListResponse);
		List<BookResponseBody> books = bookListResponse.getBody();
		Assertions.assertNotNull(books);
		Assertions.assertEquals(1, bookListResponse.getBody().size());
		book = books.get(0);
		Assertions.assertNotNull(book);
		Assertions.assertEquals(book.getId().toString(), uuid);
		Assertions.assertEquals(book.getTitle(), harry_potter);
		Assertions.assertEquals(book.getISBN(), isbn);
		Assertions.assertEquals(book.getCost(), cost);
		Assertions.assertNull(book.getTakenByUserID());

		//get Book by ISBN
		bookResponse = bookService.getBookByISBN(isbn);
		Assertions.assertNotNull(bookResponse);
		book = bookResponse.getBody();
		Assertions.assertNotNull(book);
		Assertions.assertEquals(book.getId().toString(), uuid);
		Assertions.assertEquals(book.getTitle(), harry_potter);
		Assertions.assertEquals(book.getISBN(), isbn);
		Assertions.assertEquals(book.getCost(), cost);
		Assertions.assertNull(book.getTakenByUserID());

		//update Book
		String updatedName = harry_potter + "_updated";
		bookReqBody = new BookRequestBody(uuid, updatedName, isbn, cost);
		bookResponse = bookService.saveBook(bookReqBody);
		Assertions.assertNotNull(bookResponse);
		book = bookResponse.getBody();
		Assertions.assertNotNull(book);
		Assertions.assertEquals(book.getId().toString(), uuid);
		Assertions.assertEquals(book.getTitle(), updatedName);
		Assertions.assertEquals(book.getISBN(), isbn);
		Assertions.assertEquals(book.getCost(), cost);
		Assertions.assertNull(book.getTakenByUserID());

		//delete Book
		bookService.deleteBookById(uuid);
		Assertions.assertEquals(bookService.getBookByID(uuid).getStatusCode(), HttpStatus.NOT_FOUND);
	}*/
}
