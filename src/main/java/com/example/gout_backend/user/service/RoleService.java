package com.example.gout_backend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.gout_backend.user.model.Role;
import com.example.gout_backend.user.repository.RoleRepository;
import com.example.gout_backend.user.repository.UserRoleRepository;

@Service
public class RoleService {

    private final Logger Logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Iterable<Role> getAllRole(){
        var availableRoles = roleRepository.findAll();
        Logger.info("availableRoles: {}", availableRoles);
        return availableRoles;
    }
    
}
