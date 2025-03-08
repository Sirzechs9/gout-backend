package com.example.gout_backend.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.gout_backend.user.model.User;

public interface UserRepositoy extends CrudRepository<User, Integer>{

}
