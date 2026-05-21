package com.library.lms.mapper;

import com.library.lms.dto.request.RegisterRequest;
import com.library.lms.dto.response.MemberResponse;
import com.library.lms.entity.Member;
import com.library.lms.entity.Role;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T12:02:30+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.dateOfBirth( request.getDateOfBirth() );
        member.email( request.getEmail() );
        member.firstName( request.getFirstName() );
        member.lastName( request.getLastName() );
        member.membershipType( request.getMembershipType() );
        member.phone( request.getPhone() );

        return member.build();
    }

    @Override
    public MemberResponse toResponse(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberResponse memberResponse = new MemberResponse();

        memberResponse.setRoleName( memberRoleName( member ) );
        memberResponse.setCreatedAt( member.getCreatedAt() );
        memberResponse.setDateOfBirth( member.getDateOfBirth() );
        memberResponse.setEmail( member.getEmail() );
        memberResponse.setFirstName( member.getFirstName() );
        memberResponse.setId( member.getId() );
        memberResponse.setLastName( member.getLastName() );
        memberResponse.setMemberCode( member.getMemberCode() );
        memberResponse.setMembershipExpiry( member.getMembershipExpiry() );
        memberResponse.setMembershipType( member.getMembershipType() );
        memberResponse.setPhone( member.getPhone() );
        memberResponse.setStatus( member.getStatus() );

        return memberResponse;
    }

    private String memberRoleName(Member member) {
        if ( member == null ) {
            return null;
        }
        Role role = member.getRole();
        if ( role == null ) {
            return null;
        }
        String name = role.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
