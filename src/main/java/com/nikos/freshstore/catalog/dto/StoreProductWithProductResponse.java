package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.ProductImage;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StoreProductWithProductResponse {
    private UUID id;
    private BigDecimal price;
    private Integer stockEstimate;
    private boolean isAvailable;
    private LocalDateTime lastSyncedAt;
    private ProductSummary product;

    @Data
    public static class ProductSummary {
        private UUID id;
        private String name;
        private String slug;
        private String brand;
        private String unitType;
        private Float unitSize;
        private String primaryImage;
    }

    public static StoreProductWithProductResponse from(StoreProduct storeProduct) {
        StoreProductWithProductResponse response = new StoreProductWithProductResponse();
        response.id = storeProduct.getId();
        response.price = storeProduct.getPrice();
        response.stockEstimate = storeProduct.getStockEstimate();
        response.isAvailable = storeProduct.isAvailable();
        response.lastSyncedAt = storeProduct.getLastSyncedAt();
        response.product = toProductSummary(storeProduct);
        return response;
    }

    public static ProductSummary toProductSummary(StoreProduct storeProduct) {
        ProductSummary productSummary = new ProductSummary();
        productSummary.id = storeProduct.getProduct().getId();
        productSummary.name = storeProduct.getProduct().getName();
        productSummary.slug = storeProduct.getProduct().getSlug();
        productSummary.brand = storeProduct.getProduct().getBrand();
        productSummary.unitType = storeProduct.getProduct().getUnitType();
        productSummary.unitSize = storeProduct.getProduct().getUnitSize();
        storeProduct.getProduct().getImages()
                .stream()
                .filter(ProductImage::isPrimary)
                .findFirst()
                .ifPresent(img -> productSummary.primaryImage = img.getUrl());
        return productSummary;
    }
}