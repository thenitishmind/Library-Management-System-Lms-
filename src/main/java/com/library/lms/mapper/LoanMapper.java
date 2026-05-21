package com.library.lms.mapper;

import com.library.lms.dto.request.LoanRequest;
import com.library.lms.dto.response.LoanResponse;
import com.library.lms.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "bookCopy", ignore = true)
    @Mapping(target = "issuedBy", ignore = true)
    @Mapping(target = "returnedTo", ignore = true)
    @Mapping(target = "issueDate", ignore = true)
    @Mapping(target = "dueDate", ignore = true)
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "renewalCount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "fine", ignore = true)
    Loan toEntity(LoanRequest request);

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", expression = "java(loan.getMember().getFirstName() + ' ' + loan.getMember().getLastName())")
    @Mapping(target = "bookCopyId", source = "bookCopy.id")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    @Mapping(target = "barcode", source = "bookCopy.barcode")
    @Mapping(target = "overdue", expression = "java(loan.isOverdue())")
    @Mapping(target = "overdueDays", expression = "java(loan.overdueDays())")
    LoanResponse toResponse(Loan loan);
}
