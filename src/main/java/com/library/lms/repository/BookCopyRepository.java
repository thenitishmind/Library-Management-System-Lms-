package com.library.lms.repository;

import com.library.lms.entity.BookCopy;
import com.library.lms.entity.enums.BookCopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    Optional<BookCopy> findByBarcode(String barcode);

    List<BookCopy> findByBookId(Long bookId);

    List<BookCopy> findByBookIdAndStatus(Long bookId, BookCopyStatus status);

    @Query("SELECT COUNT(c) FROM BookCopy c WHERE c.book.id = :bookId AND c.status = 'AVAILABLE'")
    long countAvailableByBookId(@Param("bookId") Long bookId);
}
