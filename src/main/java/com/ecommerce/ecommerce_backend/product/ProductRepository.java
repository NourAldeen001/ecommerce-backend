package com.ecommerce.ecommerce_backend.product;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(Long id);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findAll(); // List of product with his category

    @EntityGraph(attributePaths = {"category"})
    List<Product> findByCategoryId(Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryIdAndIdNot(String name, Long categoryId, Long id);
}
