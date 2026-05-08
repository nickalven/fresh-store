package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.constraints.PrimaryImageConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @Size(max = 100)
    private String brand;

    @Size(max = 50)
    private String barcode;

    @NotBlank(message = "Unit type is required")
    @Size(max = 20)
    private String unitType;

    @NotNull(message = "Unit size is required")
    @Positive(message = "Unit size must be positive")
    private Float unitSize;

    @Valid
    private List<ProductAttributeRequest> attributes = new ArrayList<>();

    @Valid
    @PrimaryImageConstraint
    private List<ProductImageRequest> images = new ArrayList<>();
}
