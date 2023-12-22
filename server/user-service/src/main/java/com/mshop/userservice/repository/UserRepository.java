package com.mshop.userservice.repository;

import com.mshop.userservice.repository.entitity.User;
import com.mshop.userservice.repository.entitity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = "SELECT ui FROM UserInfo ui WHERE ui.userId = :userId")
    UserInfo getUserInfoById(@Param("userId") String userId);
}
