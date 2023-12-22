package com.mshop.orderservice.repository.entity.order;

import java.util.ArrayList;
import java.util.List;

public enum OrderStatus {
    // người bán hàng chuẩn bị đơn hàng.
    PREPARE,
    // Người bán hàng sẵn sàng giao cho shipper.
    READY,
    // Shipper đã nhận hàng.
    TAKE_ORDER,
    // Đang vận chuyển đơn hàng.
    SHIPPING,
    // Shipper đã giao hàng đến khách hàng
    DELIVERED,
    // User đã nhận được hàng
    RECEIVED,
    // Đơn bị huỷ
    CANCELED;

    public static OrderStatus fromValue(String value) {
        if (value == null) return null;
        switch (value.toLowerCase()) {
            case "prepare":
                return PREPARE;
            case "ready":
                return READY;
            case "take_order":
                return TAKE_ORDER;
            case "shipping":
                return SHIPPING;
            case "delivered":
                return DELIVERED;
            case "received":
                return RECEIVED;
            case "canceled":
                return CANCELED;
            default:
                return null;
        }
    }

    public static List<OrderStatus> fromValues(String values) {
        String[] listValues = values.split(",");
        List<OrderStatus> result = new ArrayList<>();
        for(String value : listValues) {
            result.add(fromValue(value.trim()));
        }
        return result;
    }
}
