package com.intern.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for returning a JWT token")
public class AuthResponse {

    @Schema(description = "The generated JSON Web Token (JWT) for authentication.",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGkiLCJpYXQiOjE2Nz...")
    private String token;
}