package com.example.jwtoauthtoken.repository;


import com.example.jwtoauthtoken.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByUsername(String username);
}