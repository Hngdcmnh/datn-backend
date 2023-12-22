package com.mshop.orderservice.repository;

import com.mshop.orderservice.repository.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
