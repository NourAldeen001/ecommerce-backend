package com.ecommerce.ecommerce_backend.order.dto;

import com.ecommerce.ecommerce_backend.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private BigDecimal total;
    private LocalDateTime createdAt;
}
