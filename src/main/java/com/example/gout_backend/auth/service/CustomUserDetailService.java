package com.example.gout_backend.auth.service;


import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.gout_backend.auth.dto.AuthenticatedUser;
import com.example.gout_backend.auth.repository.UserLoginRepository;
import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.tourcompany.repository.TourCompanyLoginRepository;
import com.example.gout_backend.tourcompany.repository.TourCompanyRespository;
import com.example.gout_backend.user.repository.UserRoleRepository;


@Primary 
@Service
public class CustomUserDetailService implements UserDetailsService{
    private UserLoginRepository userLoginRepository;
    private UserRoleRepository userRoleRepository;
    private TourCompanyLoginRepository tourCompanyLoginRepository;
    private TourCompanyRespository tourCompanyRespository;

    public CustomUserDetailService(UserLoginRepository userLoginRepository, UserRoleRepository userRoleRepository, TourCompanyLoginRepository tourCompanyLoginRepository, TourCompanyRespository tourCompanyRespository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleRepository = userRoleRepository;
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
        this.tourCompanyRespository = tourCompanyRespository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // If it's email -> TourCompany
        if (isEmail(username)){
            return userFlow(username);
        }
        // otherwise, UserLogin
        return tourCompanyFlow(username);
        
    }

    private boolean isEmail(String username){
        var result = EmailValidator.getInstance().isValid(username);
        System.out.println(result);
        return result;
    }


    private AuthenticatedUser userFlow(String username){
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



    private AuthenticatedUser tourCompanyFlow(String username){
        var tourcompanyLogin = tourCompanyLoginRepository.findOneByUsername(username)
                    .orElseThrow(()-> new EntityNotFoundException(String.format("Credential for %s not found", username)));

        return new AuthenticatedUser(tourcompanyLogin.id(), tourcompanyLogin.username(), tourcompanyLogin.password(), RoleEnum.COMPANY );
    }

}
