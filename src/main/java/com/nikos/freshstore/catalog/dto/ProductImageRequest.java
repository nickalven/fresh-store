package com.nikos.freshstore.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    @JsonProperty("isPrimary")
    private boolean isPrimary = false;

    @Min(value = 0, message = "Sort order must be zero or positive")
    private int sortOrder = 0;
}