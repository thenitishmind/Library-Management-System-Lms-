package com.library.lms.service.impl;

import com.library.lms.dto.request.BookRequest;
import com.library.lms.dto.response.BookResponse;
import com.library.lms.dto.response.PagedResponse;
import com.library.lms.entity.*;
import com.library.lms.entity.enums.BookStatus;
import com.library.lms.exception.BusinessException;
import com.library.lms.exception.ResourceNotFoundException;
import com.library.lms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BookCopyRepository bookCopyRepository;

    @Cacheable("books")
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        return toPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        return toResponse(book);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookWithAvailability(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        BookResponse response = toResponse(book);
        response.setAvailableCopies(bookCopyRepository.countAvailableByBookId(id));
        return response;
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> searchBooks(String q, String isbn, String authorName,
                                                    Long categoryId, Pageable pageable) {
        Page<Book> page;
        if (authorName != null) {
            page = bookRepository.findByAuthorName(authorName, pageable);
        } else if (categoryId != null) {
            page = bookRepository.findByCategory(categoryId, pageable);
        } else {
            page = bookRepository.searchBooks(q, isbn, pageable);
        }
        return toPagedResponse(page);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException("Book with ISBN already exists: " + request.getIsbn());
        }
        Book book = buildBook(request);
        book = bookRepository.save(book);
        log.info("Added book: {} ({})", book.getTitle(), book.getIsbn());
        return toResponse(book);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @CacheEvict(value = "books", allEntries = true)
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book", id));

        if (!book.getIsbn().equals(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BusinessException("ISBN already in use: " + request.getIsbn());
        }

        applyRequest(book, request);
        book = bookRepository.save(book);
        log.info("Updated book id={}", id);
        return toResponse(book);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    @CacheEvict(value = "books", allEntries = true)
    public void archiveBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book", id));
        book.setStatus(BookStatus.ARCHIVED);
        bookRepository.save(book);
        log.info("Archived book id={}", id);
    }

    private Book buildBook(BookRequest req) {
        Book book = new Book();
        applyRequest(book, req);
        return book;
    }

    private void applyRequest(Book book, BookRequest req) {
        book.setIsbn(req.getIsbn());
        book.setTitle(req.getTitle());
        book.setSubtitle(req.getSubtitle());
        book.setLanguage(req.getLanguage() != null ? req.getLanguage() : "en");
        book.setEdition(req.getEdition());
        book.setPublicationYear(req.getPublicationYear());
        book.setTotalPages(req.getTotalPages());
        book.setDescription(req.getDescription());
        book.setCoverImageUrl(req.getCoverImageUrl());
        book.setStatus(BookStatus.ACTIVE);

        if (req.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(req.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher", req.getPublisherId()));
            book.setPublisher(publisher);
        }

        if (req.getAuthorIds() != null) {
            Set<Author> authors = new HashSet<>(authorRepository.findAllById(req.getAuthorIds()));
            book.setAuthors(authors);
        }

        if (req.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(req.getCategoryIds()));
            book.setCategories(categories);
        }
    }

    private BookResponse toResponse(Book book) {
        BookResponse resp = new BookResponse();
        resp.setId(book.getId());
        resp.setIsbn(book.getIsbn());
        resp.setTitle(book.getTitle());
        resp.setSubtitle(book.getSubtitle());
        resp.setLanguage(book.getLanguage());
        resp.setEdition(book.getEdition());
        resp.setPublicationYear(book.getPublicationYear());
        resp.setTotalPages(book.getTotalPages());
        resp.setDescription(book.getDescription());
        resp.setCoverImageUrl(book.getCoverImageUrl());
        resp.setStatus(book.getStatus());
        resp.setCreatedAt(book.getCreatedAt());
        resp.setUpdatedAt(book.getUpdatedAt());

        if (book.getPublisher() != null) {
            BookResponse.PublisherSummary ps = new BookResponse.PublisherSummary();
            ps.setId(book.getPublisher().getId());
            ps.setName(book.getPublisher().getName());
            resp.setPublisher(ps);
        }

        resp.setAuthors(book.getAuthors().stream().map(a -> {
            BookResponse.AuthorSummary as = new BookResponse.AuthorSummary();
            as.setId(a.getId());
            as.setFirstName(a.getFirstName());
            as.setLastName(a.getLastName());
            return as;
        }).collect(Collectors.toSet()));

        resp.setCategories(book.getCategories().stream().map(c -> {
            BookResponse.CategorySummary cs = new BookResponse.CategorySummary();
            cs.setId(c.getId());
            cs.setName(c.getName());
            cs.setSlug(c.getSlug());
            return cs;
        }).collect(Collectors.toSet()));

        resp.setTotalCopies(book.getCopies().size());
        resp.setAvailableCopies(bookCopyRepository.countAvailableByBookId(book.getId()));

        return resp;
    }

    private PagedResponse<BookResponse> toPagedResponse(Page<Book> page) {
        return PagedResponse.<BookResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();
    }
}
