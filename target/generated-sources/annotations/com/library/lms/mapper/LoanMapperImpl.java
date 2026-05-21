package com.library.lms.mapper;

import com.library.lms.dto.request.LoanRequest;
import com.library.lms.dto.response.LoanResponse;
import com.library.lms.entity.Book;
import com.library.lms.entity.BookCopy;
import com.library.lms.entity.Loan;
import com.library.lms.entity.Member;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T12:02:30+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class LoanMapperImpl implements LoanMapper {

    @Override
    public Loan toEntity(LoanRequest request) {
        if ( request == null ) {
            return null;
        }

        Loan.LoanBuilder loan = Loan.builder();

        loan.notes( request.getNotes() );

        return loan.build();
    }

    @Override
    public LoanResponse toResponse(Loan loan) {
        if ( loan == null ) {
            return null;
        }

        LoanResponse loanResponse = new LoanResponse();

        loanResponse.setMemberId( loanMemberId( loan ) );
        loanResponse.setBookCopyId( loanBookCopyId( loan ) );
        loanResponse.setBookTitle( loanBookCopyBookTitle( loan ) );
        loanResponse.setBarcode( loanBookCopyBarcode( loan ) );
        loanResponse.setDueDate( loan.getDueDate() );
        loanResponse.setId( loan.getId() );
        loanResponse.setIssueDate( loan.getIssueDate() );
        loanResponse.setNotes( loan.getNotes() );
        loanResponse.setRenewalCount( loan.getRenewalCount() );
        loanResponse.setReturnDate( loan.getReturnDate() );
        loanResponse.setStatus( loan.getStatus() );

        loanResponse.setMemberName( loan.getMember().getFirstName() + ' ' + loan.getMember().getLastName() );
        loanResponse.setOverdue( loan.isOverdue() );
        loanResponse.setOverdueDays( loan.overdueDays() );

        return loanResponse;
    }

    private Long loanMemberId(Loan loan) {
        if ( loan == null ) {
            return null;
        }
        Member member = loan.getMember();
        if ( member == null ) {
            return null;
        }
        Long id = member.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long loanBookCopyId(Loan loan) {
        if ( loan == null ) {
            return null;
        }
        BookCopy bookCopy = loan.getBookCopy();
        if ( bookCopy == null ) {
            return null;
        }
        Long id = bookCopy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String loanBookCopyBookTitle(Loan loan) {
        if ( loan == null ) {
            return null;
        }
        BookCopy bookCopy = loan.getBookCopy();
        if ( bookCopy == null ) {
            return null;
        }
        Book book = bookCopy.getBook();
        if ( book == null ) {
            return null;
        }
        String title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private String loanBookCopyBarcode(Loan loan) {
        if ( loan == null ) {
            return null;
        }
        BookCopy bookCopy = loan.getBookCopy();
        if ( bookCopy == null ) {
            return null;
        }
        String barcode = bookCopy.getBarcode();
        if ( barcode == null ) {
            return null;
        }
        return barcode;
    }
}
