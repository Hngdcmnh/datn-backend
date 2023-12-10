package com.sudoo.userservice.service;

import com.sudo248.domain.exception.ApiException;
import com.sudoo.userservice.controller.dto.UserDto;
import com.sudoo.userservice.controller.dto.UserInfoDto;
import com.sudoo.userservice.repository.entitity.User;

import java.util.List;

public interface UserService {
    UserDto getUser(String userId);

    List<UserInfoDto> getAllUserInfo() throws ApiException;
    UserInfoDto getUserInfo(String userId) throws ApiException;
    UserDto postUser(UserDto userDto) throws ApiException;
    UserDto putUser(String userId, UserDto userDto) throws ApiException;
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
