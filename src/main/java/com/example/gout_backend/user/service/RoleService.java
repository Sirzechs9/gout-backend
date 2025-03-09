package com.example.gout_backend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.common.exception.EntityNotFoundException;
import com.example.gout_backend.user.model.Role;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.model.UserRole;
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
    
    public UserRole bindingNewUser(int id, RoleEnum role){
        AggregateReference<User, Integer> userId = AggregateReference.to(id);
        AggregateReference<Role, Integer> roleId = AggregateReference.to(role.getId());
        var prepareRole = new UserRole(null, userId, roleId);
        return userRoleRepository.save(prepareRole);
    }

        public void deleteRoleByUserId(int userId){
        var userRole = userRoleRepository.findOneByUserId(AggregateReference.to(userId))
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Role for user %d not found", userId)));
        userRoleRepository.delete(userRole);
    }

}
