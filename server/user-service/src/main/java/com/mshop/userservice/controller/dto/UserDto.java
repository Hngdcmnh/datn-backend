package com.mshop.userservice.controller.dto;

import com.mshop.userservice.repository.entitity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userId;

    private String fullName;

    private String emailOrPhoneNumber;

    private LocalDate dob;

    private String bio;

    private String avatar;

    private String cover;

    private AddressDto address;

    private Gender gender;
}
