package com.example.gout_backend.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.gout_backend.user.dto.UserCreationDto;
import com.example.gout_backend.user.dto.UserInfoDto;
import com.example.gout_backend.user.dto.UserUpdateDto;
import com.example.gout_backend.user.model.User;



public interface  UserService {


    Page<User> getUsersByFirstNameKeyword(String keyword, Pageable pageable);

    User getUserById(int id);

    UserInfoDto getUserDtoById(int id);

    UserInfoDto createUser(UserCreationDto body);

    UserInfoDto updateUser(int id, UserUpdateDto body);

    boolean deleteUserById(int id);
}
