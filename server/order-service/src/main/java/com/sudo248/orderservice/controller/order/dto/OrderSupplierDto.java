package com.sudo248.orderservice.controller.order.dto;

import com.sudo248.orderservice.repository.entity.order.Shipment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSupplierDto {
    private String orderSupplierId;
    private String supplierId;
    private String promotionId;
    private double promotionValue;
    private Shipment shipment;
    private List<CartProductDto> cartProducts;
}
