package com.example.gout_backend.user.dto;

import org.springframework.data.annotation.Id;

public record UserInfoDto( 
    @Id Integer id,
    String firstName,
    String lastName,
    String phoneNumber) {
   
}
