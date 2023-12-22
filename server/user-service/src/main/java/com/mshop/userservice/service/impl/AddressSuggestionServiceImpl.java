package com.mshop.userservice.service.impl;

import com.sudo248.domain.exception.ApiException;
import com.mshop.userservice.controller.dto.AddressSuggestionDto;
import com.mshop.userservice.controller.dto.ghn.address.GHNDistrictDto;
import com.mshop.userservice.controller.dto.ghn.address.GHNProvinceDto;
import com.mshop.userservice.controller.dto.ghn.address.GHNResponse;
import com.mshop.userservice.controller.dto.ghn.address.GHNWardDto;
import com.mshop.userservice.external.GHNService;
import com.mshop.userservice.service.AddressSuggestionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressSuggestionServiceImpl implements AddressSuggestionService {
    private final GHNService ghnService;

    public AddressSuggestionServiceImpl(GHNService ghnService) {
        this.ghnService = ghnService;
    }

    @Override
    public List<AddressSuggestionDto> getProvinces() throws ApiException {
        GHNResponse<List<GHNProvinceDto>> response = ghnService.getGHNProvince();
        if (response.getCode() != 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, response.getMessage());
        }
        return response.getData().stream().map(GHNProvinceDto::toAddressSuggestionDto).collect(Collectors.toList());
    }

    @Override
    public List<AddressSuggestionDto> getDistricts(int provinceId) throws ApiException {
        GHNResponse<List<GHNDistrictDto>> response = ghnService.getGHNDistrict(provinceId);
        if (response.getCode() != 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, response.getMessage());
        }
        return response.getData().stream().map(GHNDistrictDto::toAddressSuggestionDto).collect(Collectors.toList());
    }

    @Override
    public List<AddressSuggestionDto> getWards(int districtId) throws ApiException {
        GHNResponse<List<GHNWardDto>> response = ghnService.getGHNWard(districtId);
        if (response.getCode() != 200) {
            throw new ApiException(HttpStatus.BAD_REQUEST, response.getMessage());
        }
        return response.getData().stream().map(GHNWardDto::toAddressSuggestionDto).collect(Collectors.toList());
    }
}
