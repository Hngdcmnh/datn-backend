package com.mshop.orderservice.repository.entity.order;

import com.mshop.orderservice.external.GHNService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Shipment {
    @Column(name = "shipment_type")
    private ShipmentType shipmentType = GHNService.DEFAULT_SHIPMENT_TYPE;
    @Column(name = "delivery_time")
    private int deliveryTime = 0;
    @Column(name = "received_date_time")
    private LocalDateTime receivedDateTime = null;
    @Column(name = "shipment_price")
    private double shipmentPrice = 0.0;
}
