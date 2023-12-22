package com.mshop.userservice.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpsertUserProductDto {
    private String productId;
    private String userId;

    public UpsertUserProductDto(String productId) {
        this.productId = productId;
        this.userId = null;
    }
}
