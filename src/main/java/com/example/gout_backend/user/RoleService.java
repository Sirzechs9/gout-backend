package com.example.gout_backend.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.gout_backend.user.model.Role;
import com.example.gout_backend.user.repository.RoleRepository;

@Service
public class RoleService {

    private final Logger Logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Iterable<Role> getAllRole(){
        var availableRoles = roleRepository.findAll();
        Logger.info("availableRoles: {}", availableRoles);
        return availableRoles;
    }
    
}
