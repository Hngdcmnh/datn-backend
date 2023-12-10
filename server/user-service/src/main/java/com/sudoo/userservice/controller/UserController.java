package com.sudoo.userservice.controller;

import com.sudo248.domain.base.BaseResponse;
import com.sudo248.domain.common.Constants;
import com.sudo248.domain.util.Utils;
import com.sudoo.userservice.controller.dto.UserDto;
import com.sudoo.userservice.controller.dto.UserInfoDto;
import com.sudoo.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getUser(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    ) {
        return Utils.handleException(() -> {
            UserDto userDto = userService.getUser(userId);
            return BaseResponse.ok(userDto);
        });
    }

    @GetMapping("/info/all")
    public ResponseEntity<BaseResponse<?>> getAllCustomer(
    ) {
        return Utils.handleException(() -> {
            List<UserInfoDto> userInfoDtos = userService.getAllUserInfo();
            return BaseResponse.ok(userInfoDtos);
        });
    }
    @GetMapping("/info")
    public ResponseEntity<BaseResponse<?>> getUserInfo(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    ) {
        return Utils.handleException(() -> {
            UserInfoDto userInfoDto = userService.getUserInfo(userId);
            return BaseResponse.ok(userInfoDto);
        });
    }

    @GetMapping("/internal")
    public UserDto getUserInternal(
            @RequestHeader(Constants.HEADER_USER_ID) String userId
    ) {
        return userService.getUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<?>> getOtherUser(
            @PathVariable("userId") String userId
    ) {
        return Utils.handleException(() -> {
            UserDto userDto = userService.getUser(userId);
            return BaseResponse.ok(userDto);
        });
    }

    @PostMapping
    public ResponseEntity<BaseResponse<?>> postUser(
            @RequestBody UserDto userDto
    ) {
        return Utils.handleException(() -> {
            UserDto _userDto = userService.postUser(userDto);
            return BaseResponse.ok(_userDto);
        });
    }

    @PutMapping
    public ResponseEntity<BaseResponse<?>> putUser(
            @RequestHeader(Constants.HEADER_USER_ID) String userId,
            @RequestBody UserDto userDto
    ) {
        return Utils.handleException(() -> {
            UserDto _userDto = userService.putUser(userId, userDto);
            return BaseResponse.ok(_userDto);
        });
    }
}