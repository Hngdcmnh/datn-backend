package com.mshop.userservice.repository.entitity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Table(name = "users")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email_or_phone_number")
    private String emailOrPhoneNumber;

    private LocalDate dob;

    private String bio;

    private String avatar;

    private String cover;

    private Gender gender;

    @Column(name = "address_id")
    private String addressId;
}
