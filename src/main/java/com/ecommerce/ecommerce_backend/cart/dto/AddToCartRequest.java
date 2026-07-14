package com.ecommerce.ecommerce_backend.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddToCartRequest {

    @NotNull(message = "Product ID is required")
    @Schema(description = "ID of product you want to add to cart", example = "1")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity you want from product", example = "3")
    private Integer quantity;
}
