package com.ecommerce.ecommerce_backend.cart;

import com.ecommerce.ecommerce_backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true) // FK
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) // Field
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
    }
}
