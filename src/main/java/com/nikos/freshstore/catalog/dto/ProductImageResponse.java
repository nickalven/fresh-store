package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Category;
import com.nikos.freshstore.catalog.domain.ProductImage;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductImageResponse {

    private UUID id;
    private String url;
    private String altText;
    private boolean isPrimary;
    private int sortOrder;

    public static ProductImageResponse from(ProductImage productImage) {
        ProductImageResponse response = new ProductImageResponse();
        response.setId(productImage.getId());
        response.setUrl(productImage.getUrl());
        response.setAltText(productImage.getAltText());
        response.setSortOrder(productImage.getSortOrder());
        response.setPrimary(productImage.isPrimary());
        return response;
    }
}
