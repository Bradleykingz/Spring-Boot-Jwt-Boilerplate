package com.retroshift.springjwtauthboilerplate.spring.persistence;

import com.retroshift.springjwtauthboilerplate.spring.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
}
