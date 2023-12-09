package com.sudo248.orderservice.controller.order;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.common.Constants;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import com.sudo248.orderservice.controller.order.dto.PatchOrderSupplierDto;
import com.sudo248.orderservice.controller.order.dto.UpsertOrderDto;
import com.sudo248.orderservice.controller.order.dto.OrderDto;
import com.sudo248.orderservice.controller.order.dto.UpsertOrderPromotionDto;
import com.sudo248.orderservice.repository.entity.order.StatisticRevenueCondition;
import com.sudo248.orderservice.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/orders")
public class OrderController {
    final String patternDate = "^\\d{1,2}/\\d{1,2}/\\d{4}";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<?>> createOrder(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody UpsertOrderDto upsertOrderDto
    ) {
        return Utils.handleException(() -> {
            OrderDto savedOrder = orderService.createOrder(userId, upsertOrderDto);
            return BaseResponse.ok(savedOrder);
        });
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<?>> getAllOrders(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    ) {
        return Utils.handleException(() -> {
            List<OrderDto> list = orderService.getAllOrders();
            return BaseResponse.ok(list);
        });
    }

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getOrderByUserId(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    ) {
        return Utils.handleException(() -> {
            List<OrderDto> list = orderService.getOrdersByUserId(userId);
            return BaseResponse.ok(list);
        });
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseResponse<?>> getOrderById(@PathVariable String orderId) throws ApiException {
        return Utils.handleException(() -> {
            OrderDto orderDto = orderService.getOrderById(orderId);
            return BaseResponse.ok(orderDto);
        });
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<BaseResponse<?>> deleteOrderById(@PathVariable String orderId) {
        return Utils.handleException(() -> {
            boolean res = orderService.deleteOrder(orderId);
            if (res)
                return BaseResponse.ok(orderId);
            return BaseResponse.ok(null);
        });
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<BaseResponse<?>> cancelOrderById(
            @PathVariable String orderId
    ) {
        return Utils.handleException(() -> {
            boolean res = orderService.cancelOrderByUser(orderId);
            if (res)
                return BaseResponse.ok(orderId);
            return BaseResponse.ok(null);
        });
    }

    @PatchMapping("/{orderId}/{field}/{fieldId}")
    public ResponseEntity<BaseResponse<?>> updateOrderByCase(
            @PathVariable(name = "orderId") String orderId,
            @PathVariable(name = "field") String field,
            @PathVariable(name = "fieldId") String fieldId
    ) {
        return Utils.handleException(() -> {
            return BaseResponse.ok(orderService.updateOrderByField(orderId, field, fieldId));
        });
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<BaseResponse<?>> updateOrderStatus(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @PathVariable(name = "orderId") String orderId,
            @RequestBody PatchOrderSupplierDto patchOrderSupplierDto
    ) {
        return Utils.handleException(() -> BaseResponse.ok(orderService.patchOrderSupplier(userId, orderId, patchOrderSupplierDto)));
    }

    @PatchMapping("/{orderId}/promotion")
    public ResponseEntity<BaseResponse<?>> updateOrderPromotion(
            @PathVariable(name = "orderId") String orderId,
            @RequestBody UpsertOrderPromotionDto upsertOrderPromotionDto
    ) {
        return Utils.handleException(() -> BaseResponse.ok(orderService.updateOrderPromotion(orderId, upsertOrderPromotionDto)));
    }

    @PatchMapping("/{orderId}/payment/{paymentId}")
    ResponseEntity<BaseResponse<?>> patchPaymentOrder(
            @PathVariable("orderId") String orderId,
            @PathVariable("paymentId") String paymentId
    ) {
        return Utils.handleException(() -> {
            orderService.updateOrderPayment(orderId, paymentId);
            return BaseResponse.ok(true);
        });
    }

    // statistic
    @GetMapping("/statistic/revenue")
    ResponseEntity<BaseResponse<?>> getRevenueFromTo(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestParam(value = "key", required = false, defaultValue = "") String key,
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to
    ) {
        return Utils.handleException(() -> {
            if (Pattern.matches(patternDate, from) && Pattern.matches(patternDate, to)) {
                return BaseResponse.ok(orderService.statisticRevenue(
                        userId,
                        StatisticRevenueCondition.fromString(key),
                        LocalDate.parse(from, formatter),
                        LocalDate.parse(to, formatter)
                ));
            } else {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Wrong format date");
            }
        });
    }
}
