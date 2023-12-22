package com.mshop.orderservice.controller.order.dto;

import com.mshop.orderservice.repository.entity.order.OrderStatus;
import com.mshop.orderservice.repository.entity.order.Shipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
// for customer
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSupplierDto {
    private String orderSupplierId;
    private SupplierInfoDto supplier;
    private PromotionDto promotion;
    private double totalPrice;
    private Double revenue;
    private Shipment shipment;
    private LocalDateTime expectedReceiveDateTime;
    private OrderStatus status;
    private List<OrderCartProductDto> orderCartProducts;
}
