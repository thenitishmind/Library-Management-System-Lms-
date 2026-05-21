package com.library.lms.mapper;

import com.library.lms.dto.request.RegisterRequest;
import com.library.lms.dto.response.MemberResponse;
import com.library.lms.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberCode", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "membershipExpiry", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "loans", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "fines", ignore = true)
    Member toEntity(RegisterRequest request);

    @Mapping(target = "roleName", source = "role.name")
    MemberResponse toResponse(Member member);
}
