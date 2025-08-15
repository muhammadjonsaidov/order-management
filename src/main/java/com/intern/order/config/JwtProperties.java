package com.intern.order.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    @NotBlank(message = "JWT secret key must not be blank")
    private String secret;

    @Positive(message = "JWT expiration time must be a positive number")
    private long expirationMs;
}