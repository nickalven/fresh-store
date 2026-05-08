package com.nikos.freshstore.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStoreProductRequest {

    @NotNull(message = "Store is required")
    private UUID storeId;

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    private Integer stockEstimate;

    @NotNull(message = "Availability is required")
    private Boolean isAvailable;
}