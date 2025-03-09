package com.example.gout_backend.auth.service;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.gout_backend.auth.dto.AuthenticatedUser;
import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.Role;
import com.example.gout_backend.user.repository.UserLoginRepository;
import com.example.gout_backend.user.repository.UserRoleRepository;


@Primary 
@Service
public class CustomUserDetailService implements UserDetailsService{
    private UserLoginRepository userLoginRepository;
    private UserRoleRepository userRoleRepository;

    public CustomUserDetailService(UserLoginRepository userLoginRepository, UserRoleRepository userRoleRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            var userLogin = userLoginRepository.findOneByEmail(username)
                                .orElseThrow(()-> new EntityNotFoundException(String.format("username %s not found", username)));
        var userId = userLogin.userId().getId();
        var userRole = userRoleRepository.findOneByUserId(AggregateReference.to(userId))
                                .orElseThrow(()-> new EntityNotFoundException(String.format("role of username: %s not found", username)));
        var role = RoleEnum.CONSUMER;
        if (userRole.roleId().getId() == RoleEnum.ADMIN.getId()) {
            role = RoleEnum.ADMIN;
        }

        return new AuthenticatedUser(userId, userLogin.email(), userLogin.password() , role);

    
        
    }

}
