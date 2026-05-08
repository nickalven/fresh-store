package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.StoreProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public class StoreProductWithStoreResponse {
    private UUID id;
    private BigDecimal price;
    private Integer stockEstimate;
    private boolean isAvailable;
    private LocalDateTime lastSyncedAt;
    private StoreSummary store;

    @Data
    public static class StoreSummary {
        private UUID id;
        private String name;
        private String slug;
        private String city;
        private BigDecimal lat;
        private BigDecimal lng;
    }

    public static StoreProductWithStoreResponse from(StoreProduct storeProduct) {
        StoreProductWithStoreResponse response = new StoreProductWithStoreResponse();
        response.id = storeProduct.getId();
        response.price = storeProduct.getPrice();
        response.stockEstimate = storeProduct.getStockEstimate();
        response.isAvailable = storeProduct.isAvailable();
        response.lastSyncedAt = storeProduct.getLastSyncedAt();
        response.store = toStoreSummary(storeProduct);
        return response;
    }

    public static StoreSummary toStoreSummary(StoreProduct storeProduct) {
        StoreSummary storeSummary = new StoreSummary();
        storeSummary.id = storeProduct.getStore().getId();
        storeSummary.name = storeProduct.getStore().getName();
        storeSummary.slug = storeProduct.getStore().getSlug();
        storeSummary.lat = storeProduct.getStore().getLat();
        storeSummary.lng = storeProduct.getStore().getLng();
        return storeSummary;
    }

}