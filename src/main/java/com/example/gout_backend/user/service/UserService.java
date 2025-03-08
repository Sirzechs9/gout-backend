package com.example.gout_backend.user.service;

import com.example.gout_backend.user.dto.UserCreationDto;
import com.example.gout_backend.user.dto.UserInfoDto;
import com.example.gout_backend.user.dto.UserUpdateDto;
import com.example.gout_backend.user.model.User;



public interface  UserService {


    User getUserById(int id);

    UserInfoDto getUserDtoById(int id);

    UserInfoDto createUser(UserCreationDto body);

    UserInfoDto updateUser(int id, UserUpdateDto body);

    boolean deleteUserById(int id);
}
