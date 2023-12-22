package com.mshop.orderservice.controller.order.dto.ghn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GHNResponse<T> {
    private int code;
    private String message;
    private T data;
}
