package com.ecommerce.ecommerce_backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be not exceed 50 characters")
    @Schema(description = "User's first name", example = "Ahmed")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be not exceed 50 characters")
    @Schema(description = "User's last name", example = "Ali")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid",
    regexp = "[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @Schema(description = "Unique email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Password - minimum 8 characters", example = "password123")
    private String password;
}
