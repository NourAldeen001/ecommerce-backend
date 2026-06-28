package com.ecommerce.ecommerce_backend.order;

import com.ecommerce.ecommerce_backend.cart.Cart;
import com.ecommerce.ecommerce_backend.cart.CartItem;
import com.ecommerce.ecommerce_backend.cart.CartRepository;
import com.ecommerce.ecommerce_backend.order.dto.OrderItemResponse;
import com.ecommerce.ecommerce_backend.order.dto.OrderResponse;
import com.ecommerce.ecommerce_backend.product.Product;
import com.ecommerce.ecommerce_backend.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse placeOrder(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found for user: " + userId)
                );

        if(cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot place an order with an empty cart");
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal totalPrice = BigDecimal.ZERO;

        for(CartItem cartItem : cart.getCartItems()) {

            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Product not found: " + cartItem.getProduct().getId())
                    );

            if(product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product: " + product.getName() +
                        ". Available: " + product.getStockQuantity() +
                        ", Requested: " + cartItem.getQuantity()
                );
            }

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product); // After User take product with x quantity

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.addItem(orderItem);

            totalPrice = totalPrice.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id)
                );

        if(order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException(
                    "Only PENDING orders can be cancelled. Current status: " + order.getStatus()
            );
        }

        for(OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        return mapToResponse(orderRepository.save(order));
    }

    /// Helper------------------------------------------
    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .items(itemResponses)
                .total(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse mapToItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .quantity(orderItem.getQuantity())
                .subtotal(
                        orderItem.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )
                .build();
    }


}
