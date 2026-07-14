package com.ecommerce.ecommerce_backend.user;

import com.ecommerce.ecommerce_backend.role.Role;
import com.ecommerce.ecommerce_backend.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        return  roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
