package com.ecommerce.ecommerce_backend.auth;

import com.ecommerce.ecommerce_backend.auth.dto.AuthResponse;
import com.ecommerce.ecommerce_backend.auth.dto.LoginRequest;
import com.ecommerce.ecommerce_backend.common.security.JwtService;
import com.ecommerce.ecommerce_backend.role.Role;
import com.ecommerce.ecommerce_backend.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        try {
            // throws exception if credentials are wrong
            // internally calls UserDetailsService + PasswordEncoder
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch (AuthenticationException ex) {
            log.warn("Failed login attempt - email={} reason={}",
                    request.getEmail(), ex.getClass().getSimpleName());
            throw ex;
        }

        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(user);

        log.info("User logged in successfully - email={}", user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }
}
