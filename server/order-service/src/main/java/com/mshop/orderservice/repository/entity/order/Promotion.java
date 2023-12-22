package com.mshop.orderservice.repository.entity.order;

import com.mshop.orderservice.controller.order.dto.PromotionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Promotion {
    @Column(name = "promotion_id")
    private String promotionId;
    @Column(name = "promotion_name")
    private String promotionName;
    @Column(name = "promotion_value")
    private Double promotionValue = 0.0;

    public PromotionDto toPromotionDto() {
        return new PromotionDto(
                promotionId,
                promotionName,
                promotionValue
        );
    }
}
