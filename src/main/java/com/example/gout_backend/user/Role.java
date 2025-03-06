package com.example.gout_backend.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("role")
public record Role(@Id Integer id, String name) {
    
}
