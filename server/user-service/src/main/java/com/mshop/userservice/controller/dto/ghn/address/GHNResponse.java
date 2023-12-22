package com.mshop.userservice.controller.dto.ghn.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GHNResponse <T> {
    private int code;
    private String message;
    private T data;
}
