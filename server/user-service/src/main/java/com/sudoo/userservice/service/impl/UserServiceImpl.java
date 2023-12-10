package com.sudoo.userservice.service.impl;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.exception.ApiException;
import com.sudo248.domain.util.Utils;
import com.sudoo.userservice.controller.dto.AddressDto;
import com.sudoo.userservice.controller.dto.FirebaseUserDto;
import com.sudoo.userservice.controller.dto.UserDto;
import com.sudoo.userservice.controller.dto.UserInfoDto;
import com.sudoo.userservice.internal.ProductService;
import com.sudoo.userservice.repository.UserRepository;
import com.sudoo.userservice.repository.entitity.User;
import com.sudoo.userservice.repository.entitity.UserInfo;
import com.sudoo.userservice.service.AddressService;
import com.sudoo.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AddressService addressService;

    private final ProductService productService;

    public UserServiceImpl(UserRepository userRepository, AddressService addressService, ProductService productService) {
        this.userRepository = userRepository;
        this.addressService = addressService;
        this.productService = productService;
    }

    @Override
    public UserDto getUser(String userId) {
        User user = userRepository.getReferenceById(userId);
        return toDto(user);
    }

    @Override
    public List<UserInfoDto> getAllUserInfo() throws ApiException {
        return userRepository.findAll().stream().map(userInfo -> {
            return new UserInfoDto(
                    userInfo.getUserId(),
                    userInfo.getFullName(),
                    userInfo.getAvatar());
        }).collect(Collectors.toList());
    }

    @Override
    public UserInfoDto getUserInfo(String userId) throws ApiException {
        UserInfo userInfo = userRepository.getUserInfoById(userId);
        if (userInfo == null) throw new ApiException(HttpStatus.NOT_FOUND, "Not found user " + userId);
        return new UserInfoDto(
                userInfo.getUserId(),
                userInfo.getFullName(),
                userInfo.getAvatar()
        );
    }

    @Override
    public UserDto postUser(UserDto userDto) {
        User user = toEntity(userDto);
        AddressDto addressDto = userDto.getAddress();
        AddressDto savedAddress = addressService.postAddress(addressDto);
        user.setAddressId(savedAddress.getAddressId());
        userRepository.save(user);
        try {
            createAllUserProduct(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toDto(user);
    }

    public void createAllUserProduct(String userId) throws ApiException {
        ResponseEntity<BaseResponse<Boolean>> response = productService.upsertUserProductForAllProduct(userId);
        if (response.getStatusCode() != HttpStatus.OK || !response.hasBody()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Something went wrong!!");
        }
    }

    @Override
    public UserDto putUser(String userId, UserDto userDto) throws ApiException {
        userDto.setUserId(userId);

        User user = userRepository.getReferenceById(userId);
        user.setFullName(userDto.getFullName());
        user.setDob(userDto.getDob());
        user.setBio(userDto.getBio());
        user.setAvatar(userDto.getAvatar());
        user.setCover(userDto.getCover());
        user.setGender(userDto.getGender());

        AddressDto addressDto = userDto.getAddress();
        if (addressDto.getAddressId() == null || addressDto.getAddressId().isEmpty()) {
            addressDto.setAddressId(user.getAddressId());
        }
        addressService.putAddress(addressDto);
        userRepository.save(user);
//        firebaseService.upsertFirebaseUser(createUserFirebaseFromUser(user));
        return toDto(user);
    }

    @Override
    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setFullName(user.getFullName());
        userDto.setEmailOrPhoneNumber(user.getEmailOrPhoneNumber());
        userDto.setDob(user.getDob());
        userDto.setBio(user.getBio());
        userDto.setAvatar(user.getAvatar());
        userDto.setCover(user.getCover());
        userDto.setGender(user.getGender());

        AddressDto addressDto = addressService.getAddress(user.getAddressId());
        userDto.setAddress(addressDto);
        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setUserId(Utils.createIdOrElse(userDto.getUserId()));
        user.setFullName(userDto.getFullName());
        user.setEmailOrPhoneNumber(userDto.getEmailOrPhoneNumber());
        user.setDob(userDto.getDob() != null ? userDto.getDob() : LocalDate.now());
        user.setBio(userDto.getBio());
        user.setAvatar(userDto.getAvatar());
        user.setCover(userDto.getCover());
        user.setGender(userDto.getGender());
        AddressDto addressDto = userDto.getAddress();
        return user;
    }

    private FirebaseUserDto createUserFirebaseFromUser(User user) {
        return new FirebaseUserDto(
                user.getUserId(),
                user.getFullName(),
                user.getAvatar()
        );
    }
}
