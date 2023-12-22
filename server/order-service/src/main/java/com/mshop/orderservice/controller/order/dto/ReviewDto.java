package com.mshop.orderservice.controller.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private String userProductId;
    private float rate;
    private boolean isReviewed;
    private String comment;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private UserInfoDto userInfo;
    private ProductInfoDto productInfo;
}