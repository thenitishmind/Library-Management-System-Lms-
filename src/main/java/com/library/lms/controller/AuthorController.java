package com.library.lms.controller;

import com.library.lms.entity.Author;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.AuthorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Author management")
public class AuthorController {

    private final AuthorRepository authorRepository;

    @GetMapping
    @Operation(summary = "Get all authors")
    public ResponseEntity<Page<Author>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(authorRepository.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get author by ID")
    public ResponseEntity<Author> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author", id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @Operation(summary = "Create author")
    public ResponseEntity<Author> create(@RequestBody Author author) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorRepository.save(author));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @Operation(summary = "Update author")
    public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody Author updated) {
        Author author = authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Author", id));
        author.setFirstName(updated.getFirstName());
        author.setLastName(updated.getLastName());
        author.setBiography(updated.getBiography());
        author.setBirthDate(updated.getBirthDate());
        author.setNationality(updated.getNationality());
        author.setPhotoUrl(updated.getPhotoUrl());
        return ResponseEntity.ok(authorRepository.save(author));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete author")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
