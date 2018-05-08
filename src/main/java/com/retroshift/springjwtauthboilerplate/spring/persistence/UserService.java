package com.retroshift.springjwtauthboilerplate.spring.persistence;

import com.retroshift.springjwtauthboilerplate.spring.entity.UserEntity;

public interface UserService {

    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
}
