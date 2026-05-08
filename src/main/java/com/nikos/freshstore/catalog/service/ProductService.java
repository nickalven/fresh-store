package com.nikos.freshstore.catalog.service;

import com.nikos.freshstore.catalog.dto.ProductRequest;
import com.nikos.freshstore.catalog.dto.ProductResponse;
import com.nikos.freshstore.catalog.dto.ProductSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductSummaryResponse> getSummaryProducts();

    List<ProductSummaryResponse> getProductsByCategory(UUID categoryId);

    ProductResponse getProductById(UUID id);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(ProductRequest productRequest, UUID id);

    void deleteProduct(UUID id);
}
