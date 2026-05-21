package com.library.lms.repository;

import com.library.lms.entity.Book;
import com.library.lms.entity.BookCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Book> searchByTitle(@Param("q") String query, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.authors a " +
           "WHERE LOWER(CONCAT(a.firstName,' ',a.lastName)) LIKE LOWER(CONCAT('%',:name,'%'))")
    Page<Book> findByAuthorName(@Param("name") String name, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.categories cat " +
           "WHERE cat.id = :categoryId")
    Page<Book> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b " +
           "WHERE b.id = :bookId " +
           "AND EXISTS (SELECT c FROM BookCopy c WHERE c.book = b AND c.status = 'AVAILABLE')")
    Optional<Book> findByIdWithAvailableCopies(@Param("bookId") Long bookId);

    @Query("SELECT c FROM BookCopy c " +
           "WHERE c.book.id = :bookId AND c.status = 'AVAILABLE'")
    List<BookCopy> findAvailableCopies(@Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b " +
           "WHERE (:q IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%',:q,'%'))) " +
           "AND (:isbn IS NULL OR b.isbn = :isbn)")
    Page<Book> searchBooks(
        @Param("q") String query,
        @Param("isbn") String isbn,
        Pageable pageable
    );
}
