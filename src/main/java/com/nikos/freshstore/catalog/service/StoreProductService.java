package com.nikos.freshstore.catalog.service;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import com.nikos.freshstore.catalog.dto.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StoreProductService {

    StoreProductResponse createStoreProduct(CreateStoreProductRequest createStoreProductRequest);

    StoreProductResponse updateStoreProduct(UpdateStoreProductRequest updateStoreProductRequest, UUID id);

    List<StoreProductWithProductResponse> getStoreProducts(UUID storeId);


    List<StoreProductWithStoreResponse> getProductStores(UUID productId);

    StoreProductResponse getStoreProduct(UUID id);

    void deleteStoreProduct(UUID id);

}
