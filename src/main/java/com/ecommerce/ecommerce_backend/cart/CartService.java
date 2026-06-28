package com.ecommerce.ecommerce_backend.cart;

import com.ecommerce.ecommerce_backend.cart.dto.AddToCartRequest;
import com.ecommerce.ecommerce_backend.cart.dto.CartItemResponse;
import com.ecommerce.ecommerce_backend.cart.dto.CartResponse;
import com.ecommerce.ecommerce_backend.product.Product;
import com.ecommerce.ecommerce_backend.product.ProductRepository;
import com.ecommerce.ecommerce_backend.user.User;
import com.ecommerce.ecommerce_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " +
                                request.getProductId())
                );

        // Check if this product is already in the cart |
        //      found -> increase quantity by 1
        //      not found -> add product on cart with quantity 1
        var existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId());

        int requestedTotalQuantity = request.getQuantity();
        if(existingItem.isPresent()) {
            requestedTotalQuantity += existingItem.get().getQuantity();
        }

        // Validate stock before modifying anything
        if(requestedTotalQuantity > product.getStockQuantity()) {
            throw new RuntimeException(
                    "Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStockQuantity()
            );
        }

        if(existingItem.isPresent()) {
            // Same Product added again -> increase quantity by 1
            CartItem item = existingItem.get();
            item.setQuantity(requestedTotalQuantity);
        }
        else {
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not in cart: " + productId)
                );

        if(quantity > item.getProduct().getStockQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + item.getProduct().getName() +
                    ". Available: " + item.getProduct().getStockQuantity());
        }

        item.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);

        return mapToResponse(savedCart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not in cart: " + productId)
                );

        cart.removeItem(item);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    /// ---------- Helper ------------------------------------------
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException("User not found with id: " + userId)
                            );
                    Cart newCart = Cart.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getCartItems()
                .stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalPrice(total)
                .build();
    }

    private CartItemResponse mapItemToResponse(CartItem item) {

        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .unitPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
