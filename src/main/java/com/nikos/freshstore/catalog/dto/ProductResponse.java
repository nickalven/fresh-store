package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Product;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String brand;
    private String barcode;
    private String unitType;
    private Float unitSize;
    private boolean isActive;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProductImageResponse> images;
    private List<ProductAttributeResponse> attributes;

    public static ProductResponse from(Product product) {
        ProductResponse response = new ProductResponse();
        response.id = product.getId();
        response.name = product.getName();
        response.description = product.getDescription();
        response.categoryName = product.getName();
        response.setBrand(product.getBrand());
        response.setBarcode(product.getBarcode());
        response.setUnitType(product.getUnitType());
        response.setActive(product.isActive());
        response.setUnitSize(product.getUnitSize());
        response.images = product.getImages().stream()
                .map(ProductImageResponse::from)
                .toList();
        response.attributes = product.getAttributes().stream()
                .map(ProductAttributeResponse::from)
                .toList();
        response.createdAt = product.getCreatedAt();
        response.setUpdatedAt(product.getUpdatedAt());



        return response;
    }

}
