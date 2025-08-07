package com.intern.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request body for creating a new customer order")
public class CreateOrderRequest {
    @Schema(description = "Name of the customer", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank @Size(max = 100)
    private String customerName;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank @Email
    private String customerEmail;

    @Schema(description = "List of items included in the order", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty @Valid
    private List<OrderItemRequest> orderItems;
}