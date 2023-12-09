package com.sudo248.orderservice.service.order.impl;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import com.sudo248.orderservice.controller.order.dto.*;
import com.sudo248.orderservice.controller.order.dto.ghn.*;
import com.sudo248.orderservice.controller.payment.dto.PaymentInfoDto;
import com.sudo248.orderservice.external.GHNService;
import com.sudo248.orderservice.internal.AccountService;
import com.sudo248.orderservice.internal.CartService;
import com.sudo248.orderservice.internal.ProductService;
import com.sudo248.orderservice.internal.UserService;
import com.sudo248.orderservice.repository.OrderRepository;
import com.sudo248.orderservice.repository.OrderSupplierRepository;
import com.sudo248.orderservice.repository.entity.Role;
import com.sudo248.orderservice.repository.entity.order.*;
import com.sudo248.orderservice.repository.entity.payment.Payment;
import com.sudo248.orderservice.repository.entity.payment.PaymentStatus;
import com.sudo248.orderservice.repository.entity.payment.PaymentType;
import com.sudo248.orderservice.service.order.OrderService;
import com.sudo248.orderservice.service.payment.PaymentService;
import com.sudo248.orderservice.utils.StringUtils;
import com.sudoo.domain.utils.IdentifyCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.of;

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

    private final AccountService accountService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserService userService,
            CartService cartService,
            PaymentService paymentService,
            OrderSupplierRepository orderSupplierRepository,
            GHNService ghnService,
            ProductService productService,
            AccountService accountService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.orderSupplierRepository = orderSupplierRepository;
        this.ghnService = ghnService;
        this.productService = productService;
        this.accountService = accountService;
    }

    @Override
    public List<OrderDto> getOrdersByUserId(String userId) throws ApiException {
        List<Order> orders = orderRepository.getOrdersByUserId(userId);
        return orders.stream().filter((e) -> e.getPayment() != null).map(
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
    public List<OrderDto> getAllOrders() throws ApiException {
        List<OrderDto> orderDtos = orderRepository.getOrdersBySupplierId("4e794c286eac2074a2be3822e8cb3c53").stream().map(order -> {
                    CartDto cart;
                    List<OrderCartProductDto> cartProducts = new ArrayList<>();

                    UserDto user = getUserById(order.getUserId());
                    try {
                        cart = getOrderCartById(order.getCartId(), "");
                        cartProducts = cart.getCartProducts();
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    return OrderDto.builder()
                            .orderId(order.getOrderId())
                            .cartId(order.getCartId())
                            .payment(PaymentInfoDto.builder()
                                    .paymentId(IdentifyCreator.INSTANCE.create())
                                    .amount(0.0)
                                    .status(PaymentStatus.PENDING)
                                    .paymentType(PaymentType.Cash.name())
                                    .paymentDateTime(order.getCreatedAt())
                                    .build()
                            )
                            .promotion(PromotionDto.builder()
                                    .promotionId("")
                                    .name("")
                                    .value(0.0)
                                    .build())
                            .user(user)
                            .status(order.getStatus())
                            .address(order.getAddress())
                            .totalPrice(order.getTotalPrice())
                            .totalShipmentPrice(order.getShipment().getShipmentPrice())
                            .totalPromotionPrice(0.0)
                            .finalPrice(order.getTotalPrice() + order.getShipment().getShipmentPrice())
                            .createdAt(order.getCreatedAt())
                            .cartProducts(cartProducts)
                            .build();
                }
        ).collect(Collectors.toList());
        return orderDtos;

    }

    @Override
    public OrderDto getOrderById(String orderId) throws ApiException {
        Order order = orderRepository.getReferenceById(orderId);


        final CartDto cart = getOrderCartById(order.getCartId(), "");
        order.setCartProducts(cart.getCartProducts());
        return toDto(order);
    }

    @Override
    public OrderDto createOrder(String userId, UpsertOrderDto upsertOrderDto) throws ApiException {

        Order.OrderBuilder builder = Order.builder()
                .orderId(Utils.createIdOrElse(upsertOrderDto.getOrderId()))
                .cartId(upsertOrderDto.getCartId())
                .userId(userId);

        CartDto cart = getProcessingCart(userId);
        UserDto user = getUserById(userId);

        builder.totalPrice(cart.getTotalPrice());
        builder.address(user.getAddress().getFullAddress());

        PromotionDto promotionDto = null;
        if (!StringUtils.isNullOrEmpty(upsertOrderDto.getPromotionId())) {
            promotionDto = getPromotionById(upsertOrderDto.getPromotionId());
            builder.promotionId(promotionDto.getPromotionId());
        } else {
            builder.totalPromotionPrice(0.0);
        }

        Order order = builder.build();

        final LocalDateTime createdAt = LocalDateTime.now();
        final Map<String, List<OrderCartProductDto>> groupBySupplier = cart.getCartProducts().stream().collect(Collectors.groupingBy(orderCartProductDto ->
                orderCartProductDto.getProduct().getSupplierId()
        ));

        final List<OrderCartProductDto> cartProducts = cart.getCartProducts();

        int totalLength = cartProducts.get(0).getProduct().getLength();
        int totalWeight = 0;
        int totalWidth = cartProducts.get(0).getProduct().getLength();
        int totalHeight = 0;
        double totalPrice = 0.0;


        for (OrderCartProductDto cartProduct : cartProducts) {
            totalPrice += cartProduct.getTotalPrice();
            totalWeight += cartProduct.getProduct().getWeight() * cartProduct.getQuantity();
            totalHeight += cartProduct.getProduct().getHeight() * cartProduct.getQuantity();
        }

        SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();

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

        order.setTotalPrice(totalPrice);
        order.setShipment(calculateShipment(supplier.getGhnShopId(), calculateFeeRequest, calculateExpectedTimeRequest));


        order.calculateTotalPromotionPrice(promotionDto, null);
        order.setFinalPrice(order.getTotalPrice() + order.getShipment().getShipmentPrice());
        order.setSupplierId(supplier.getSupplierId());
        order.setTotalShipmentPrice(order.getShipment().getShipmentPrice());
        order.setStatus(OrderStatus.PREPARE);
        order.setCreatedAt(createdAt);

        orderRepository.save(order);

        return OrderDto.builder()
                .orderId(order.getOrderId())
                .cartId(order.getCartId())
                .payment(null)
                .promotion(PromotionDto.builder().build())
                .user(user)
                .address(order.getAddress())
                .totalPrice(order.getTotalPrice())
                .totalShipmentPrice(order.getShipment().getShipmentPrice())
                .totalPromotionPrice(0.0)
                .finalPrice(order.getTotalPrice() + order.getShipment().getShipmentPrice())
                .createdAt(createdAt)
                .cartProducts(cartProducts)
//                .orderSuppliers(order.getOrderSuppliers().stream().map(this::toOrderSupplierDto).collect(Collectors.toList()))
                .build();
    }

    @Override
    public boolean deleteOrder(String orderId) {
        orderRepository.deleteById(orderId);
        return true;
    }

    @Override
    public boolean cancelOrderByUser(String orderId) throws ApiException {
        final Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Not found order " + orderId);
        cartService.deleteProcessingCart(order.get().getUserId());
        orderRepository.deleteById(orderId);
        return false;
    }

    @Override
    public OrderDto toDto(Order order) throws ApiException {
        final LocalDateTime orderCreatedAt;
        if (order.getOrderSuppliers().isEmpty()) {
            orderCreatedAt = LocalDateTime.now();
        } else {
            orderCreatedAt = order.getOrderSuppliers().get(0).getCreatedAt();
        }
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
                .createdAt(orderCreatedAt)
                .cartProducts(order.getCartProducts())
                .build();

    }

    public Order toOrder(OrderDto orderDto) throws ApiException {
        SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();

        final LocalDateTime orderCreatedAt;
        orderCreatedAt = LocalDateTime.now();
        return Order.builder()
                .orderId(orderDto.getOrderId())
                .supplierId(supplier.getSupplierId())
                .cartId(orderDto.getCartId())
                .payment(paymentService.getPaymentById(orderDto.getPayment().getPaymentId()))
                .totalPromotionPrice(orderDto.getTotalPromotionPrice())
                .promotionId(orderDto.getPromotion().getPromotionId())
                .userId(orderDto.getUser().getUserId())
                .status(orderDto.getStatus())
                .address(orderDto.getAddress())
                .totalPrice(orderDto.getTotalPrice())
                .totalShipmentPrice(orderDto.getTotalShipmentPrice())
                .totalPromotionPrice(orderDto.getTotalPromotionPrice())
                .finalPrice(orderDto.getFinalPrice())
                .createdAt(orderCreatedAt)
                .cartProducts(orderDto.getCartProducts())
                .build();

    }


    @Override
    public OrderSupplierDto toOrderSupplierDto(OrderSupplier orderSupplier) {
        if (orderSupplier.getSupplier() == null) {
            orderSupplier.setSupplier(productService.getSupplierById(orderSupplier.getSupplierId()).getData());
        }
        PromotionDto promotionDto = null;
        if (!StringUtils.isNullOrEmpty(orderSupplier.getPromotionId())) {
            try {
                promotionDto = getPromotionById(orderSupplier.getPromotionId());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        return new OrderSupplierDto(
                orderSupplier.getOrderSupplierId(),
                orderSupplier.getSupplier(),
                promotionDto,
                orderSupplier.getTotalPrice(),
                orderSupplier.getRevenue(),
                orderSupplier.getShipment(),
                orderSupplier.getCreatedAt().plusSeconds(orderSupplier.getShipment().getDeliveryTime() / 1000),
                orderSupplier.getStatus(),
                orderSupplier.getCartProducts()
        );
    }

    @Override
    public Map<String, ?> updateOrderByField(String orderId, String field, String id) throws ApiException {
        Order order = orderRepository.getReferenceById(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put(field, id);
        switch (field) {
            case "promotion":
                PromotionDto promotionDto = getPromotionById(id);
                order.setPromotionId(id);
                order.setTotalPromotionPrice(promotionDto.getValue());
                order.calculateFinalPrice();
                orderRepository.save(order);
                result.putAll(getUpdateOrderMap(order));
        }
        return result;
    }

    @Override
    public UpsertOrderPromotionDto updateOrderPromotion(String orderId, UpsertOrderPromotionDto upsertOrderPromotionDto) throws ApiException {
        PromotionDto promotionDto = getPromotionById(upsertOrderPromotionDto.getPromotionId());
        Optional<Order> _order = orderRepository.findById(orderId);
        if (_order.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Not found order" + orderId);
        final Order order = _order.get();
        if (StringUtils.isNullOrEmpty(upsertOrderPromotionDto.getOrderSupplierId())) {
            order.setPromotionId(upsertOrderPromotionDto.getPromotionId());
        } else {
            // update promotion for order supplier
        }
        order.calculateTotalPromotionPrice(promotionDto, null);
        order.calculateFinalPrice();
        orderRepository.save(order);
        return new UpsertOrderPromotionDto(
                upsertOrderPromotionDto.getPromotionId(),
                upsertOrderPromotionDto.getOrderSupplierId(),
                order.getTotalPrice(),
                order.getTotalPromotionPrice(),
                order.getFinalPrice()
        );
    }

    private Map<String, Object> getUpdateOrderMap(Order order) {
        return Map.of(
                "totalPrice", order.getTotalPrice(),
                "totalPromotionPrice", order.getTotalPromotionPrice(),
                "totalShipmentPrice", order.getTotalShipmentPrice(),
                "finalPrice", order.getFinalPrice()
        );
    }

    @Override
    public void updateOrderPayment(String orderId, String paymentId) {
        Order order = orderRepository.getReferenceById(orderId);
        Payment payment = paymentService.getPaymentById(paymentId);
        order.setPayment(payment);
        orderRepository.save(order);
    }

    @Override
    public OrderDto getOrderByOrderSupplierIdAndSupplierFromUserId(String orderSupplierId, String userId) throws ApiException {
        final SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();
        Optional<OrderSupplier> orderSupplier = orderSupplierRepository.findById(orderSupplierId);
        if (orderSupplier.isEmpty())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found order supplier " + orderSupplierId);
        return getOrderOfSupplier(orderSupplier.get(), supplier);
    }

    @Override
    public List<OrderSupplierInfoDto> getListOrderSupplierInfoFromUserId(String userId, OrderStatus status) throws ApiException {
        final SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();

        return orderRepository.getOrderBySupplierId(supplier.getSupplierId()).stream().map(
                        (order -> {
                            final UserDto userDto = getUserById(order.getUserId());
                            String paymentType = null;
                            LocalDateTime paymentDateTime = null;
                            if (order.getPayment() != null) {
                                paymentType = order.getPayment().getPaymentType();
                                paymentDateTime = order.getPayment().getPaymentDateTime();
                            }
                            return OrderSupplierInfoDto.builder()
                                    .orderSupplierId("")
                                    .supplierId(supplier.getSupplierId())
                                    .supplierName(supplier.getName())
                                    .userFullName(userDto.getFullName())
                                    .userPhoneNumber(userDto.getEmailOrPhoneNumber())
                                    .paymentType(paymentType)
                                    .paymentDateTime(paymentDateTime)
                                    .status(order.getStatus())
                                    .address(order.getAddress())
                                    .expectedReceiveDateTime(order.getCreatedAt().plusSeconds(order.getShipment().getDeliveryTime() / 1000))
                                    .totalPrice(order.getTotalPrice())
                                    .createdAt(order.getCreatedAt())
                                    .build();
                        }))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderCartProductDto> getListOrderUserInfoByUserId(String userId, List<OrderStatus> status) throws ApiException {
        final SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();

        List<Order> orders = orderRepository.getOrdersByUserId(userId);

        List<Order> orderResults = new ArrayList<>();

        List<OrderCartProductDto> orderCartProductsDtos = new ArrayList<>();

        if (status != null) {
            orderResults = orders.stream().filter((e) -> status.contains(e.getStatus())).collect(Collectors.toList());
        } else {
            orderResults = orders;
        }

        for (Order order : orderResults) {
            final CartDto cart = getOrderCartById(order.getCartId(), "");
            List<OrderCartProductDto> orderCartProductDtosOfCart = cart.getCartProducts();
            orderCartProductsDtos.addAll(orderCartProductDtosOfCart);
        }

        return orderCartProductsDtos;
    }

    @Override
    public Map<String, Object> patchOrderSupplier(String userId, String orderId, PatchOrderSupplierDto patchOrderSupplierDto) throws ApiException {

        final SupplierInfoDto supplier = SupplierInfoDto.builder()
                .supplierId("4e794c286eac2074a2be3822e8cb3c53").ghnShopId(190464).name("Hoang Duc Minh").avatar("").contactUrl("")
                .address(
                        AddressDto.builder()
                                .addressId("4160257bb44d4e479037eb3162adac7f")
                                .provinceID(233).districtID(1615).wardCode("270102").provinceName("Ninh Bình").districtName("Thành phố Ninh Bình").wardName("Phường Đông Thành")
                                .build()
                )
                .rate(0)
                .build();

        Order order = orderRepository.getReferenceById(orderId);
        final CartDto cart = getOrderCartById(order.getCartId(), "");
        order.setCartProducts(cart.getCartProducts());
        Map<String, Object> response = new HashMap<>();

        if (patchOrderSupplierDto.getStatus() != null) {
            order.setStatus(patchOrderSupplierDto.getStatus());
            if (patchOrderSupplierDto.getStatus() == OrderStatus.RECEIVED
            ) {
                upsertUserProduct(userId, order.getCartId(), supplier.getSupplierId());
            }
            response.put("status", patchOrderSupplierDto.getStatus());
        }

        if (patchOrderSupplierDto.getReceivedDateTime() != null) {
            order.setStatus(patchOrderSupplierDto.getStatus());
        }
        orderRepository.save(order);

        return response;
    }

    @Override
    public RevenueStatisticData statisticRevenue(String userId, StatisticRevenueCondition condition, LocalDate from, LocalDate to) {
        final SupplierInfoDto supplier = productService.getSupplierByUserId(userId).getData();
        final List<OrderSupplier> orderSuppliers = orderSupplierRepository.getAllBySupplierIdAndCreatedAtBetween(supplier.getSupplierId(), from.atStartOfDay(), to.atTime(LocalTime.MAX));
//        orderSuppliers.sort(Comparator.comparing(OrderSupplier::getCreatedAt).reversed());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(condition.format);
        Map<LocalDate, Double> response = new HashMap<>();
        from.datesUntil(to).collect(Collectors.toList()).forEach((e) -> response.put(e, 0.0));
        double total = 0.0;
        for (OrderSupplier orderSupplier : orderSuppliers) {
            if (orderSupplier.getRevenue() != null && orderSupplier.getRevenue() > 0.0) {
                final LocalDate key = orderSupplier.getCreatedAt().toLocalDate();
                final double currentValue = response.get(key);
                response.put(key, currentValue + orderSupplier.getRevenue());
                total += orderSupplier.getTotalPrice();
            }
        }

        return RevenueStatisticData.builder()
                .data(response.entrySet().stream().collect(Collectors.toMap((entry) -> formatter.format(entry.getKey()), Map.Entry::getValue)))
                .total(total)
                .build();
    }

    private Role getRoleByUserId(String userId) throws ApiException {
        var response = accountService.getRoleByUserId(userId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found role user " + userId);
        return Objects.requireNonNull(response.getBody()).getData();
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
        ResponseEntity<BaseResponse<UserDto>> response = userService.getUser(userId);
        if (response.getStatusCodeValue() == 200) {
            return Objects.requireNonNull(response.getBody()).getData();
        }
        return new UserDto();
    }

    private CartDto getProcessingCart(String userId) throws ApiException {
        var response = cartService.getProcessingCart(userId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found processing cart for user " + userId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    private CartDto getOrderCartById(String cartId, String supplierId) throws ApiException {
        var response = cartService.getCartById(cartId, true, supplierId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found processing cart " + cartId);
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

    private List<OrderSupplier> createOrderSuppliersByCart(Order order, CartDto cart, UserDto user) throws ApiException {
        final LocalDateTime createdAt = LocalDateTime.now();
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
                    .order(order)
                    .supplierId(supplierId)
                    .supplier(supplier)
                    .promotionId(null)
                    .revenue(null)
                    .status(OrderStatus.PREPARE)
                    .cartProducts(cartProducts)
                    .createdAt(createdAt);

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

    OrderDto getOrderOfSupplier(OrderSupplier orderSupplier, SupplierInfoDto supplierInfoDto) throws ApiException {
        orderSupplier.setSupplier(supplierInfoDto);
        CartDto cart = getOrderCartById(orderSupplier.getOrder().getCartId(), supplierInfoDto.getSupplierId());
        orderSupplier.setCartProducts(cart.getCartProducts());
        Order order = orderSupplier.getOrder();
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .cartId(order.getCartId())
                .payment(paymentService.toPaymentInfoDto(order.getPayment()))
                .promotion(getPromotionById(order.getPromotionId()))
                .user(getUserById(order.getUserId()))
                .address(order.getAddress())
                .totalPrice(orderSupplier.getTotalPrice())
                .totalShipmentPrice(orderSupplier.getShipment().getShipmentPrice())
                .totalPromotionPrice(0.0)
                .finalPrice(orderSupplier.getTotalPrice())
                .createdAt(orderSupplier.getCreatedAt())
                .build();
    }

    private List<CartProductDto> getCartProductByCartId(String cartId) throws ApiException {
        var response = cartService.getCartProductByCartId(cartId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Not found cart " + cartId);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    // Tạo các đánh giá cho người dùng và sản phẩm.
    private void upsertUserProduct(String userId, String cartId, String supplierId) throws ApiException {
        var response = cartService.upsertUserProductByUserAndSupplier(
                cartId,
                userId,
                supplierId
        );
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody())
            throw new ApiException(HttpStatus.NOT_FOUND, "Something went wrong!!");
    }
}
