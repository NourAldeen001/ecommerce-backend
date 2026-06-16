package com.ecommerce.ecommerce_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private Set<String> roles;
}
