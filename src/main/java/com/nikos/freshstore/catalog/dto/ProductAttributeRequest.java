package com.nikos.freshstore.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductAttributeRequest {

    @NotBlank(message = "Attribute key is required")
    @Size(max = 50, message = "Key must not exceed 50 characters")
    private String attrKey;

    @Size(max = 255, message = "Value must not exceed 255 characters")
    private String attrValue;
}