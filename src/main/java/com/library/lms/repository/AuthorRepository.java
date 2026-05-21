package com.library.lms.repository;

import com.library.lms.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a " +
           "WHERE LOWER(CONCAT(a.firstName,' ',a.lastName)) LIKE LOWER(CONCAT('%',:name,'%'))")
    Page<Author> searchByName(@Param("name") String name, Pageable pageable);
}
