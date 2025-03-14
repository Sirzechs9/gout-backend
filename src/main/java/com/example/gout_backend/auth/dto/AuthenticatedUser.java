package com.example.gout_backend.auth.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.gout_backend.common.enumeration.RoleEnum;

public record AuthenticatedUser(
        Integer userId,
        String email,
        String password,
        RoleEnum role
) implements  UserDetails{

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch(this.role){
            case RoleEnum.ADMIN -> List.of(new SimpleGrantedAuthority(RoleEnum.ADMIN.name()));
            case RoleEnum.COMPANY -> List.of(new SimpleGrantedAuthority(RoleEnum.COMPANY.name()));
            default -> List.of(new SimpleGrantedAuthority(RoleEnum.CONSUMER.name()));
        };
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
