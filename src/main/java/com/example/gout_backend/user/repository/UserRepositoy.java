package com.example.gout_backend.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;

import com.example.gout_backend.user.model.User;

public interface UserRepositoy extends ListCrudRepository<User, Integer>{

    Page<User> findByFirstNameContaining(String keyword, Pageable pageable);
}
