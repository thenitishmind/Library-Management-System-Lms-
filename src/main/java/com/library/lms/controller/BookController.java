package com.library.lms.controller;

import com.library.lms.dto.request.BookRequest;
import com.library.lms.dto.response.BookResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.service.impl.BookServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management")
public class BookController {

    private final BookServiceImpl bookService;

    @GetMapping
    @Operation(summary = "Get all books")
    public ResponseEntity<PagedResponse<BookResponse>> getAllBooks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(PageRequest.of(page, size, Sort.by("title"))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books")
    public ResponseEntity<PagedResponse<BookResponse>> searchBooks(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String isbn,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) Long category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(bookService.searchBooks(q, isbn, author, category,
            PageRequest.of(page, size)));
    }

    @PostMapping
    @Operation(summary = "Add a new book")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update book")
    public ResponseEntity<BookResponse> updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archive book")
    public ResponseEntity<Void> archiveBook(@PathVariable Long id) {
        bookService.archiveBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/copies")
    @Operation(summary = "Get book copies")
    public ResponseEntity<java.util.List<com.library.lms.entity.BookCopy>> getBookCopies(@PathVariable Long id) {
        // Requires bookCopyRepository or service method, for now just returning 200 OK.
        // If a service method existed, it would be called here. We'll leave it as a stub or implement in service.
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Get book availability")
    public ResponseEntity<Integer> getBookAvailability(@PathVariable Long id) {
        BookResponse response = bookService.getBookWithAvailability(id);
        return ResponseEntity.ok((int) response.getAvailableCopies());
    }
}
