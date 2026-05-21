package com.library.lms.repository;

import com.library.lms.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByMemberIdAndBookId(Long memberId, Long bookId);

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);

    Page<Review> findByBookIdAndStatus(Long bookId, com.library.lms.entity.enums.ReviewStatus status, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId AND r.status = 'PUBLISHED'")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);
}
