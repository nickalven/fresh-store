package com.nikos.freshstore.catalog.mapper;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import com.nikos.freshstore.catalog.dto.CreateStoreProductRequest;
import com.nikos.freshstore.catalog.dto.UpdateStoreProductRequest;
import org.springframework.stereotype.Component;

@Component
public class StoreProductMapper {

    public StoreProduct toEntity(CreateStoreProductRequest request, Store store, Product product) {
        StoreProduct storeProduct = new StoreProduct();
        storeProduct.setStore(store);
        storeProduct.setProduct(product);
        storeProduct.setPrice(request.getPrice());
        storeProduct.setStockEstimate(request.getStockEstimate());
        storeProduct.setAvailable(request.getIsAvailable());
        return storeProduct;
    }

    public void updateEntity(StoreProduct storeProduct, UpdateStoreProductRequest request) {
        storeProduct.setPrice(request.getPrice());
        storeProduct.setStockEstimate(request.getStockEstimate());
        storeProduct.setAvailable(request.getIsAvailable());
    }
}