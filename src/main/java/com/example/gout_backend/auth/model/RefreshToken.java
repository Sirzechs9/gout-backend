package com.example.gout_backend.auth.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("refresh_token")
public record RefreshToken(

    @Id Integer Id,
    String token,
    Instant issuedDate,
    String usage,
    Integer resourceId,
    boolean isExpired

) {

}

