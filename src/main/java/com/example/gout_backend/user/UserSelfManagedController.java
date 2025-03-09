package com.example.gout_backend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.gout_backend.user.dto.UserInfoDto;
import com.example.gout_backend.user.dto.UserUpdateDto;
import com.example.gout_backend.user.model.User;
import com.example.gout_backend.user.service.UserService;

@Controller
@RequestMapping("/api/v1/me")
public class UserSelfManagedController {

 
    private final UserService userService;

    public UserSelfManagedController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserById(Authentication authentication) {
        var result = userService.getUserById(getMyId(authentication));
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<UserInfoDto> updateUser(@RequestBody @Validated UserUpdateDto body, Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var id = Integer.parseInt(jwt.getClaimAsString("sub"));
        var result = userService.updateUser(id, body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Boolean>  deleteUser(Authentication authentication) {

        userService.deleteUserById(getMyId(authentication));
        return ResponseEntity.ok(true);
    }

    private int getMyId(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        return Integer.parseInt(jwt.getClaimAsString("sub"));
    }

}
