package com.intern.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request body for creating a new product")
public class CreateProductRequest {
    @Schema(description = "Name of the product", example = "Dell XPS 15 Laptop", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name cannot be blank") @Size(min = 2, max = 100)
    private String name;

    @Schema(description = "Price of the product", example = "1999.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @DecimalMin("0.01")
    private BigDecimal price;

    @Schema(description = "Available quantity in stock", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(0)
    private Integer stock;

    @Schema(description = "Product category", example = "Electronics")
    @Size(max = 50)
    private String category;

    @Schema(description = "Indicates if the product is active and available for sale", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Boolean isActive;
}