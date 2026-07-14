package com.ecommerce.ecommerce_backend.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
