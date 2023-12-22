package com.mshop.paymentservice.repository;

import com.mshop.paymentservice.repository.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
