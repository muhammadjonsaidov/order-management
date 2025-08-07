package com.intern.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Details of a single item within an order request")
public class OrderItemRequest {
    @Schema(description = "The unique ID of the product being ordered", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long productId;

    @Schema(description = "The quantity of the product being ordered", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(1)
    private Integer quantity;
}