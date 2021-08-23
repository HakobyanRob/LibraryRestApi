package com.example.RESTfulWebService.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public interface BookRepository extends CrudRepository<BookEntity, UUID> {
    Optional<List<BookEntity>> findByTitle(String title);

    Optional<BookEntity> findByISBN(String isbn);

    Optional<List<BookEntity>> findByTitleLikeIgnoreCase(String title);
}
