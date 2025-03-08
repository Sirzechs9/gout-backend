package com.example.gout_backend.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminManagementController {


    @GetMapping
    public String getMethodName() {
        return "Hello Admin";
    }
    
}
