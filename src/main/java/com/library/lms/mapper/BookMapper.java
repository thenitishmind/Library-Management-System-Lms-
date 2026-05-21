package com.library.lms.mapper;

import com.library.lms.dto.request.BookRequest;
import com.library.lms.dto.response.BookResponse;
import com.library.lms.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Book toEntity(BookRequest request);

    @Mapping(target = "publisher.id", source = "publisher.id")
    @Mapping(target = "publisher.name", source = "publisher.name")
    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "totalCopies", ignore = true)
    BookResponse toResponse(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "copies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget Book book, BookRequest request);
}
