package com.intern.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for user login")
public class AuthRequest {

    @Schema(description = "The username of the user.", requiredMode = Schema.RequiredMode.REQUIRED, example = "ali")
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @Schema(description = "The user's password.", requiredMode = Schema.RequiredMode.REQUIRED, example = "password123")
    @NotBlank(message = "Password cannot be blank")
    private String password;
}