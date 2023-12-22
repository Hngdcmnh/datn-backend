package com.mshop.orderservice.service.order;

import com.mshop.orderservice.controller.order.dto.*;
import com.mshop.orderservice.repository.entity.order.Order;
import com.mshop.orderservice.repository.entity.order.OrderStatus;
import com.mshop.orderservice.repository.entity.order.OrderSupplier;
import com.mshop.orderservice.repository.entity.order.StatisticRevenueCondition;
import com.sudo248.domain.exception.ApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {
    List<OrderDto> getOrdersByUserId(String userId) throws ApiException;

    List<OrderDto> getAllOrders() throws ApiException;
    OrderDto getOrderById(String orderId) throws ApiException;
    OrderDto createOrder(String userId, UpsertOrderDto upsertOrderDto) throws ApiException;
    boolean deleteOrder(String orderId);

    boolean cancelOrderByUser(String orderId) throws ApiException;

    OrderDto toDto(Order order) throws ApiException;

    OrderSupplierDto toOrderSupplierDto(OrderSupplier orderSupplier) throws ApiException;

    Map<String, ?> updateOrderByField(String invoiceId, String field, String fieldId) throws ApiException;

    UpsertOrderPromotionDto updateOrderPromotion(String orderId, UpsertOrderPromotionDto upsertOrderPromotionDto) throws ApiException;

    void updateOrderPayment(String invoiceId, String paymentId);

    OrderDto getOrderByOrderSupplierIdAndSupplierFromUserId(String orderSupplierId, String userId) throws  ApiException;

    List<OrderSupplierInfoDto> getListOrderSupplierInfoFromUserId(String userId, OrderStatus status) throws  ApiException;

    List<OrderCartProductDto> getListOrderUserInfoByUserId(String userId, List<OrderStatus> status) throws ApiException;

    List<OrderDto> getAllOrderByUserId(String userId) throws ApiException;
    List<OrderStatusCartProductDto> getListOrderUserInfoByUserIdAndStatus(String userId, List<OrderStatus> status) throws ApiException;

    List<OrderCartProductReviewDto> getListOrderReviewOfCustomer(String userId, List<OrderStatus> status) throws ApiException;

    Map<String, Object> patchOrderSupplier(String userId, String orderSupplierId, PatchOrderSupplierDto patchOrderSupplierDto) throws ApiException;

    RevenueStatisticData statisticRevenue(String userId, StatisticRevenueCondition condition, LocalDate from, LocalDate to);
}
