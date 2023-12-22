package com.mshop.userservice.service;

import com.sudo248.domain.exception.ApiException;
import com.mshop.userservice.controller.dto.AddressDto;
import com.mshop.userservice.repository.entitity.Address;

public interface AddressService {

    AddressDto postAddress(AddressDto addressDto);
    AddressDto getAddress(String addressId);
    void deleteAddress(String addressId);
    AddressDto putAddress(AddressDto addressDto) throws ApiException;

    AddressDto toDto(Address address);
    Address toEntity(AddressDto addressDto);
}
