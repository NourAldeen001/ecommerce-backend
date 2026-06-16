package com.ecommerce.ecommerce_backend.auth;

import com.ecommerce.ecommerce_backend.auth.dto.AuthResponse;
import com.ecommerce.ecommerce_backend.auth.dto.LoginRequest;
import com.ecommerce.ecommerce_backend.common.security.JwtService;
import com.ecommerce.ecommerce_backend.role.Role;
import com.ecommerce.ecommerce_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {

        // throws exception if credentials are wrong
        // internally calls UserDetailsService + PasswordEncoder
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }
}
