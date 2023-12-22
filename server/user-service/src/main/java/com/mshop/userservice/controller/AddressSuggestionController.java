package com.mshop.userservice.controller;

import com.mshop.userservice.service.AddressSuggestionService;
import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.util.Utils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/addresses/suggestion")
public class AddressSuggestionController {

    private final AddressSuggestionService addressSuggestionService;

    public AddressSuggestionController(AddressSuggestionService addressSuggestionService) {
        this.addressSuggestionService = addressSuggestionService;
    }

    @GetMapping("/province")
    public ResponseEntity<BaseResponse<?>> getProvinces() {
        return Utils.handleException(() -> BaseResponse.ok(addressSuggestionService.getProvinces()));
    }

    @GetMapping("/district/{provinceId}")
    public ResponseEntity<BaseResponse<?>> getDistricts(
            @PathVariable("provinceId") int provinceId
    ) {
        return Utils.handleException(() -> BaseResponse.ok(addressSuggestionService.getDistricts(provinceId)));
    }

    @GetMapping("/ward/{districtId}")
    public ResponseEntity<BaseResponse<?>> getWards(
            @PathVariable("districtId") int districtId
    ) {
        return Utils.handleException(() -> BaseResponse.ok(addressSuggestionService.getWards(districtId)));
    }

}
