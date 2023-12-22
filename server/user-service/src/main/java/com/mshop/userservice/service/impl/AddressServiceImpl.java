package com.mshop.userservice.service.impl;

import com.mshop.userservice.controller.dto.AddressDto;
import com.mshop.userservice.repository.AddressRepository;
import com.mshop.userservice.repository.entitity.Address;
import com.mshop.userservice.service.AddressService;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressDto postAddress(AddressDto addressDto) {
        Address address = toEntity(addressDto);
        var savedAddress = addressRepository.save(address);
        return toDto(savedAddress);
    }

    @Override
    public AddressDto getAddress(String addressId) {
        Address address = addressRepository.getReferenceById(addressId);
        return toDto(address);
    }

    @Override
    public void deleteAddress(String addressId) {
        addressRepository.deleteById(addressId);
    }

    @Override
    public AddressDto putAddress(AddressDto addressDto) throws ApiException {
        if (addressDto.getAddressId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Address id must by not null");
        }
        Address oldAddress = addressRepository.getReferenceById(addressDto.getAddressId());
        oldAddress.setProvinceID(addressDto.getProvinceID());
        oldAddress.setDistrictID(addressDto.getDistrictID());
        oldAddress.setWardCode(addressDto.getWardCode());
        oldAddress.setProvinceName(addressDto.getProvinceName());
        oldAddress.setDistrictName(addressDto.getDistrictName());
        oldAddress.setWardName(addressDto.getWardName());
        oldAddress.setAddress(addressDto.getAddress());
        addressRepository.save(oldAddress);
        return toDto(oldAddress);
    }

    @Override
    public AddressDto toDto(Address address) {
        return new AddressDto(
                address.getAddressId(),
                address.getProvinceID(),
                address.getDistrictID(),
                address.getWardCode(),
                address.getProvinceName(),
                address.getDistrictName(),
                address.getWardName(),
                address.getAddress(),
                address.getFullAddress()
        );
    }

    @Override
    public Address toEntity(AddressDto addressDto) {
        return new Address(
                Utils.createIdOrElse(addressDto.getAddressId()),
                addressDto.getProvinceID(),
                addressDto.getDistrictID(),
                addressDto.getWardCode(),
                addressDto.getProvinceName(),
                addressDto.getDistrictName(),
                addressDto.getWardName(),
                addressDto.getAddress()
        );
    }
}
