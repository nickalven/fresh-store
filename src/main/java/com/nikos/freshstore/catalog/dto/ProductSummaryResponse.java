package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.ProductImage;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductSummaryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String brand;
    private String unitType;
    private Float unitSize;
    private boolean isActive;
    private String primaryImageUrl;

    public static ProductSummaryResponse from(Product product) {
        ProductSummaryResponse response = new ProductSummaryResponse();
        response.id = product.getId();
        response.name = product.getName();
        response.slug = product.getSlug();
        response.brand = product.getBrand();
        response.unitType = product.getUnitType();
        response.unitSize = product.getUnitSize();
        response.isActive = product.isActive();

        product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .ifPresent(img -> response.primaryImageUrl = img.getUrl());

        return response;
    }
}