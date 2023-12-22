package com.mshop.paymentservice.internal;


import com.mshop.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "INVOICE-SERVICE")
@Service
public interface OrderService {
    @PutMapping("/api/v1/invoice/{invoiceId}/payment/{paymentId}")
    ResponseEntity<BaseResponse<?>> updateInvoice(@PathVariable("invoiceId") String invoiceId, @PathVariable("paymentId") String paymentId);
}
