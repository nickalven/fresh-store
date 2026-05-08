package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.ProductImage;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class StoreProductResponse {
    private UUID id;
    private BigDecimal price;
    private Integer stockEstimate;
    private boolean isAvailable;
    private LocalDateTime lastSyncedAt;
    private StoreSummary store;
    private ProductSummary product;

    public static StoreProductResponse from(StoreProduct storeProduct) {
        StoreProductResponse response = new StoreProductResponse();
        response.id = storeProduct.getId();
        response.price = storeProduct.getPrice();
        response.stockEstimate = storeProduct.getStockEstimate();
        response.isAvailable = storeProduct.isAvailable();
        response.lastSyncedAt = storeProduct.getLastSyncedAt();
        response.store = StoreSummary.from(storeProduct.getStore());
        response.product = ProductSummary.from(storeProduct.getProduct());
        return response;
    }

    @Data
    public static class StoreSummary {
        private UUID id;
        private String name;
        private String slug;
        private String city;
        private BigDecimal lat;
        private BigDecimal lng;

        public static StoreSummary from(Store store) {
            StoreSummary summary = new StoreSummary();
            summary.id = store.getId();
            summary.name = store.getName();
            summary.slug = store.getSlug();
            summary.city = store.getAddress().getCity();
            summary.lat = store.getLat();
            summary.lng = store.getLng();
            return summary;
        }
    }

    @Data
    public static class ProductSummary {
        private UUID id;
        private String name;
        private String slug;
        private String brand;
        private String unitType;
        private Float unitSize;
        private String primaryImageUrl;

        public static ProductSummary from(Product product) {
            ProductSummary summary = new ProductSummary();
            summary.id = product.getId();
            summary.name = product.getName();
            summary.slug = product.getSlug();
            summary.brand = product.getBrand();
            summary.unitType = product.getUnitType();
            summary.unitSize = product.getUnitSize();
            product.getImages().stream()
                    .filter(ProductImage::isPrimary)
                    .findFirst()
                    .ifPresent(img -> summary.primaryImageUrl = img.getUrl());
            return summary;
        }
    }
}