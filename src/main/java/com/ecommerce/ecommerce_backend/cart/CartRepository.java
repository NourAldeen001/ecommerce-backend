package com.ecommerce.ecommerce_backend.cart;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
    Optional<Cart> findByUserId(Long userId);
}
