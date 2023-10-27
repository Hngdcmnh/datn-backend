package com.sudo248.orderservice.service.order.impl;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import com.sudo248.orderservice.controller.order.dto.*;
import com.sudo248.orderservice.controller.order.dto.ghn.*;
import com.sudo248.orderservice.controller.payment.dto.PaymentInfoDto;
import com.sudo248.orderservice.external.GHNService;
import com.sudo248.orderservice.internal.CartService;
import com.sudo248.orderservice.internal.ProductService;
import com.sudo248.orderservice.internal.UserService;
import com.sudo248.orderservice.repository.OrderRepository;
import com.sudo248.orderservice.repository.OrderSupplierRepository;
import com.sudo248.orderservice.repository.entity.order.*;
import com.sudo248.orderservice.repository.entity.payment.Payment;
import com.sudo248.orderservice.service.order.OrderService;
import com.sudo248.orderservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final CartService cartService;

    private final PaymentService paymentService;

    private final OrderSupplierRepository orderSupplierRepository;

    private final GHNService ghnService;

    private final ProductService productService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserService userService,
            CartService cartService,
            PaymentService paymentService,
            OrderSupplierRepository orderSupplierRepository,
            GHNService ghnService,
            ProductService productService
    ) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.orderSupplierRepository = orderSupplierRepository;
        this.ghnService = ghnService;
        this.productService = productService;
    }

    @Override
    public List<OrderDto> getOrdersByUserId(String userId) throws ApiException {
        return orderRepository.getOrdersByUserId(userId).stream().map(
                (e) -> {
                    try {
                        return toDto(e);
                    } catch (ApiException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        ).collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(String orderId) throws ApiException {
        Order order = orderRepository.getReferenceById(orderId);
        return toDto(order);
    }

    @Override
    public OrderDto createOrder(String userId, UpsertOrderDto upsertOrderDto) throws ApiException {
        Order.OrderBuilder builder = Order.builder()
                .orderId(Utils.createIdOrElse(upsertOrderDto.getOrderId()))
                .status(OrderStatus.PREPARE)
                .cartId(upsertOrderDto.getCartId())
                .userId(userId);

        CartDto cart = getCartById(upsertOrderDto.getCartId());
        UserDto user = getUserById(userId);

        builder.totalPrice(cart.getTotalPrice());
        builder.address(user.getAddress().getFullAddress());
        builder.orderSuppliers(createOrderSuppliersByCart(cart, user));
        PromotionDto promotionDto = null;
        if (upsertOrderDto.getPromotionId() != null) {
            promotionDto = getPromotionById(upsertOrderDto.getPromotionId());
            builder.promotionId(promotionDto.getPromotionId());
            builder.totalPromotionPrice(promotionDto.getValue());
        }

        Order order = builder.build();
        order.calculateTotalShipmentPrice();
        order.calculateFinalPrice();

        orderRepository.save(order);

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .cartId(order.getCartId())
                .payment(null)
                .promotion(promotionDto)
                .user(user)
                .status(order.getStatus())
                .address(order.getAddress())
                .totalPrice(order.getTotalPrice())
                .totalShipmentPrice(order.getTotalShipmentPrice())
                .totalPromotionPrice(order.getTotalPromotionPrice())
                .finalPrice(order.getFinalPrice())
                .orderSuppliers(order.getOrderSuppliers().stream().map(this::toOrderSupplierDto).collect(Collectors.toList()))
                .build();

    }

    @Override
    public boolean deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
        return true;
    }

    @Override
    public OrderDto toDto(Order order) throws ApiException {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .cartId(order.getCartId())
                .payment(paymentService.toPaymentInfoDto(order.getPayment()))
                .promotion(getPromotionById(order.getPromotionId()))
                .user(getUserById(order.getUserId()))
                .status(order.getStatus())
                .address(order.getAddress())
                .totalPrice(order.getTotalPrice())
                .totalShipmentPrice(order.getTotalShipmentPrice())
                .totalPromotionPrice(order.getTotalPromotionPrice())
                .finalPrice(order.getFinalPrice())
                .orderSuppliers(order.getOrderSuppliers().stream().map(this::toOrderSupplierDto).collect(Collectors.toList()))
                .build();

    }

    @Override
    public OrderSupplierDto toOrderSupplierDto(OrderSupplier orderSupplier) {
        if (orderSupplier.getSupplier() == null) {
            orderSupplier.setSupplier(productService.getSupplierById(orderSupplier.getSupplierId()).getData());
        }
        return new OrderSupplierDto(
                orderSupplier.getOrderSupplierId(),
                orderSupplier.getSupplier(),
                orderSupplier.getPromotionId(),
                orderSupplier.getPromotionValue(),
                orderSupplier.getTotalPrice(),
                orderSupplier.getShipment(),
                orderSupplier.getCartProducts()
        );
    }

    @Override
    public Map<String, ?> updateOrderByField(String orderId, String field, String id) throws ApiException {
        Order order = orderRepository.getReferenceById(orderId);
        switch (field) {
            case "promotion":
                PromotionDto promotionDto = getPromotionById(id);
                order.setPromotionId(id);
                order.setTotalPromotionPrice(promotionDto.getValue());
                order.calculateFinalPrice();
                orderRepository.save(order);
        }
        return Map.of(field, id);
    }

    @Override
    public void updateOrderPayment(String orderId, String paymentId) {
        Order order = orderRepository.getReferenceById(orderId);
        Payment payment = paymentService.getPaymentById(paymentId);
        order.setPayment(payment);
        orderRepository.save(order);
    }

    //Request Detail
    private PromotionDto getPromotionById(String promotionId) throws ApiException {
        if (promotionId == null) return new PromotionDto();
        var response = productService.getPromotionById(promotionId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found promotion " + promotionId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    private UserDto getUserById(String userId) {
        ResponseEntity<BaseResponse<UserDto>> response = userService.getUserInfo(userId);
        if (response.getStatusCodeValue() == 200) {
            return Objects.requireNonNull(response.getBody()).getData();
        }
        return new UserDto();
    }

    private CartDto getCartById(String cartId) throws ApiException {
        var response = cartService.getCartById(cartId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found cart " + cartId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    private PaymentInfoDto getPaymentById(String paymentId) {
        if (paymentId == null) return null;
        return paymentService.getPaymentInfo(paymentId);
    }

    private Shipment calculateShipment(int shopId, CalculateFeeRequest calculateFeeRequest, CalculateExpectedTimeRequest calculateExpectedTimeRequest) throws ApiException {
        GHNResponse<CalculateFeeDto> responseFee = ghnService.calculateFee(shopId, calculateFeeRequest);
        if (responseFee.getCode() != 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, responseFee.getMessage());
        }
        GHNResponse<CalculateExpectedTimeDto> responseExpectedTime = ghnService.calculateExpectedTime(shopId, calculateExpectedTimeRequest);
        if (responseExpectedTime.getCode() != 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, responseFee.getMessage());
        }
        return new Shipment(
                ShipmentType.values()[calculateFeeRequest.getServiceTypeId()],
                responseExpectedTime.getData().getLeadTime(),
                null,
                responseFee.getData().getTotal()
        );
    }

    private List<OrderSupplier> createOrderSuppliersByCart(CartDto cart, UserDto user) throws ApiException {
        final Map<String, List<OrderCartProductDto>> groupBySupplier = cart.getCartProducts().stream().collect(Collectors.groupingBy(orderCartProductDto ->
                orderCartProductDto.getProduct().getSupplierId()
        ));
        final ArrayList<OrderSupplier> listOrderSupplier = new ArrayList<>();

        // should use stream parallel
        for (Map.Entry<String, List<OrderCartProductDto>> entry : groupBySupplier.entrySet()) {
            String supplierId = entry.getKey();
            List<OrderCartProductDto> cartProducts = entry.getValue();

            final SupplierInfoDto supplier = productService.getSupplierById(supplierId).getData();

            final var orderSupplierBuilder = OrderSupplier.builder()
                    .orderSupplierId(Utils.createId())
                    .supplierId(supplierId)
                    .supplier(supplier)
                    .promotionId(null)
                    .promotionValue(0.0)
                    .cartProducts(cartProducts);

            // Length is same for an product
            int totalLength = cartProducts.get(0).getProduct().getLength();
            int totalWeight = 0, totalWidth = 0, totalHeight = 0;
            double totalPrice = 0.0;
            for (OrderCartProductDto cartProduct : cartProducts) {
                totalPrice += cartProduct.getTotalPrice();
                totalWeight += cartProduct.getProduct().getWeight() * cartProduct.getQuantity();
                totalWidth += cartProduct.getProduct().getWidth() * cartProduct.getQuantity();
                totalHeight += cartProduct.getProduct().getHeight() * cartProduct.getQuantity();
            }
            final CalculateFeeRequest calculateFeeRequest = CalculateFeeRequest.builder()
                    .serviceTypeId(GHNService.DEFAULT_SHIPMENT_TYPE.ordinal())
                    .fromDistrictId(supplier.getAddress().getDistrictID())
                    .fromWardCode(supplier.getAddress().getWardCode())
                    .toDistrictId(user.getAddress().getDistrictID())
                    .toWardCode(user.getAddress().getWardCode())
                    .weight(totalWeight)
                    .length(totalLength)
                    .width(totalWidth)
                    .height(totalHeight)
                    .build();

            final CalculateExpectedTimeRequest calculateExpectedTimeRequest = CalculateExpectedTimeRequest.builder()
                    .serviceId(GHNService.DEFAULT_SHIPMENT_TYPE.ordinal())
                    .fromDistrictId(supplier.getAddress().getDistrictID())
                    .fromWardCode(supplier.getAddress().getWardCode())
                    .toDistrictId(user.getAddress().getDistrictID())
                    .toWardCode(user.getAddress().getWardCode())
                    .build();

            orderSupplierBuilder
                    .totalPrice(totalPrice)
                    .shipment(calculateShipment(supplier.getGhnShopId(), calculateFeeRequest, calculateExpectedTimeRequest));

            listOrderSupplier.add(orderSupplierBuilder.build());
        }
        return listOrderSupplier;
    }

    private List<OrderSupplier> getCartProductForOrderSupplier(List<OrderSupplier> orderSuppliers, String cartId) throws ApiException {
        CartDto cart = getCartById(cartId);
        final Map<String, List<OrderCartProductDto>> groupBySupplier = cart.getCartProducts().stream().collect(Collectors.groupingBy(orderCartProductDto ->
                orderCartProductDto.getProduct().getSupplierId()
        ));
        for (OrderSupplier orderSupplier : orderSuppliers) {
            orderSupplier.setCartProducts(groupBySupplier.get(orderSupplier.getSupplierId()));
        }
        return orderSuppliers;
    }
}