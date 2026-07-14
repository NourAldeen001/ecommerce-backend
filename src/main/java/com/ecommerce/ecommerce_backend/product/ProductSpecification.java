package com.ecommerce.ecommerce_backend.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> nameOrDescriptionContains(String keyword) {
        return (root, query, criteriaBuilder) -> {
          if(keyword == null || keyword.isBlank()) return null;

          String pattern = "%" + keyword.toLowerCase() + "%";
          return criteriaBuilder.or(
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                  criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
          );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if(categoryId == null ) return null;

            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> priceGreaterThanOrEqual(BigDecimal price) {
        return (root, query, criteriaBuilder) -> {
            if(price == null) return null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> priceLessThanOrEqual(BigDecimal price) {
        return (root, query, criteriaBuilder) -> {
          if(price == null) return null;

          return criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> inStock() {
        return (root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("stockQuantity"), 0);
    }
}
