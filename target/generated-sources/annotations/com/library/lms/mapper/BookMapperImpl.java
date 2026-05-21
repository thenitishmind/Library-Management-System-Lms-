package com.library.lms.mapper;

import com.library.lms.dto.request.BookRequest;
import com.library.lms.dto.response.BookResponse;
import com.library.lms.entity.Author;
import com.library.lms.entity.Book;
import com.library.lms.entity.Category;
import com.library.lms.entity.Publisher;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T12:02:29+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public Book toEntity(BookRequest request) {
        if ( request == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.coverImageUrl( request.getCoverImageUrl() );
        book.description( request.getDescription() );
        book.edition( request.getEdition() );
        book.isbn( request.getIsbn() );
        book.language( request.getLanguage() );
        book.publicationYear( request.getPublicationYear() );
        book.subtitle( request.getSubtitle() );
        book.title( request.getTitle() );
        book.totalPages( request.getTotalPages() );

        return book.build();
    }

    @Override
    public BookResponse toResponse(Book book) {
        if ( book == null ) {
            return null;
        }

        BookResponse bookResponse = new BookResponse();

        bookResponse.setPublisher( publisherToPublisherSummary( book.getPublisher() ) );
        bookResponse.setAuthors( authorSetToAuthorSummarySet( book.getAuthors() ) );
        bookResponse.setCategories( categorySetToCategorySummarySet( book.getCategories() ) );
        bookResponse.setCoverImageUrl( book.getCoverImageUrl() );
        bookResponse.setCreatedAt( book.getCreatedAt() );
        bookResponse.setDescription( book.getDescription() );
        bookResponse.setEdition( book.getEdition() );
        bookResponse.setId( book.getId() );
        bookResponse.setIsbn( book.getIsbn() );
        bookResponse.setLanguage( book.getLanguage() );
        bookResponse.setPublicationYear( book.getPublicationYear() );
        bookResponse.setStatus( book.getStatus() );
        bookResponse.setSubtitle( book.getSubtitle() );
        bookResponse.setTitle( book.getTitle() );
        bookResponse.setTotalPages( book.getTotalPages() );
        bookResponse.setUpdatedAt( book.getUpdatedAt() );

        return bookResponse;
    }

    @Override
    public void updateEntity(Book book, BookRequest request) {
        if ( request == null ) {
            return;
        }

        book.setCoverImageUrl( request.getCoverImageUrl() );
        book.setDescription( request.getDescription() );
        book.setEdition( request.getEdition() );
        book.setIsbn( request.getIsbn() );
        book.setLanguage( request.getLanguage() );
        book.setPublicationYear( request.getPublicationYear() );
        book.setSubtitle( request.getSubtitle() );
        book.setTitle( request.getTitle() );
        book.setTotalPages( request.getTotalPages() );
    }

    protected BookResponse.PublisherSummary publisherToPublisherSummary(Publisher publisher) {
        if ( publisher == null ) {
            return null;
        }

        BookResponse.PublisherSummary publisherSummary = new BookResponse.PublisherSummary();

        publisherSummary.setId( publisher.getId() );
        publisherSummary.setName( publisher.getName() );

        return publisherSummary;
    }

    protected BookResponse.AuthorSummary authorToAuthorSummary(Author author) {
        if ( author == null ) {
            return null;
        }

        BookResponse.AuthorSummary authorSummary = new BookResponse.AuthorSummary();

        authorSummary.setFirstName( author.getFirstName() );
        authorSummary.setId( author.getId() );
        authorSummary.setLastName( author.getLastName() );

        return authorSummary;
    }

    protected Set<BookResponse.AuthorSummary> authorSetToAuthorSummarySet(Set<Author> set) {
        if ( set == null ) {
            return null;
        }

        Set<BookResponse.AuthorSummary> set1 = new LinkedHashSet<BookResponse.AuthorSummary>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Author author : set ) {
            set1.add( authorToAuthorSummary( author ) );
        }

        return set1;
    }

    protected BookResponse.CategorySummary categoryToCategorySummary(Category category) {
        if ( category == null ) {
            return null;
        }

        BookResponse.CategorySummary categorySummary = new BookResponse.CategorySummary();

        categorySummary.setId( category.getId() );
        categorySummary.setName( category.getName() );
        categorySummary.setSlug( category.getSlug() );

        return categorySummary;
    }

    protected Set<BookResponse.CategorySummary> categorySetToCategorySummarySet(Set<Category> set) {
        if ( set == null ) {
            return null;
        }

        Set<BookResponse.CategorySummary> set1 = new LinkedHashSet<BookResponse.CategorySummary>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Category category : set ) {
            set1.add( categoryToCategorySummary( category ) );
        }

        return set1;
    }
}
