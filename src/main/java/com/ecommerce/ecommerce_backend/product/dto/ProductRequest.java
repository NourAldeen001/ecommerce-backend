package com.ecommerce.ecommerce_backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "iPhone 15 Pro")
    private String name;

    @NotNull(message = "Product description cannot be null")
    @Schema(description = "Detailed product description", example = "Latest Apple smart phone with titanium frame")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than zero")
    @Schema(description = "Product price in USD", example = "999.99")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Schema(description = "Available units in stock", example = "50")
    private Integer stockQuantity;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of category this product belongs to", example = "1")
    private Long categoryId;
}
