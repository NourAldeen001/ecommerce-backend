package com.ecommerce.ecommerce_backend.category.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private int productCount;
}
