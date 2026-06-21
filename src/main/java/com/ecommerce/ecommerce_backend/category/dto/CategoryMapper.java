package com.ecommerce.ecommerce_backend.category.dto;

import com.ecommerce.ecommerce_backend.category.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "productCount", expression = "java(category.getProducts().size())")
    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequest request);
}
