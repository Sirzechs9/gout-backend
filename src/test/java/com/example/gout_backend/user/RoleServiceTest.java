package com.example.gout_backend.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.gout_backend.common.enumeration.RoleEnum;
import com.example.gout_backend.user.model.Role;
import com.example.gout_backend.user.repository.RoleRepository;


@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void shouldReturnRole(){

        var mockRole = List.of(
            new Role(1, RoleEnum.CONSUMER.name()),
            new Role(2, RoleEnum.ADMIN.name()),
            new Role(3, RoleEnum.COMPANY.name()) );

        when(roleRepository.findAll())
            .thenReturn(mockRole);

        var actual = roleService.getAllRole();
        List<Role> result = new ArrayList<>();
        actual.iterator().forEachRemaining(result::add); // iterates over all elements in actual and adds them to result using method reference (result::add is equivalent to role -> result.add(role)).

        Assertions.assertEquals(3, result.size());
    }

}
