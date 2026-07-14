package com.ecommerce.ecommerce_backend.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min= 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Schema(description = "Category name", example = "Smart Phones")
    private String name;

    @NotNull(message = "Category description cannot be null")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Detailed category description", example = "Phones have touch screens and intelligent apps")
    private String description;
}
