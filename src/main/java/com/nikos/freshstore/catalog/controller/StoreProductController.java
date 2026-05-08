package com.nikos.freshstore.catalog.controller;

import com.nikos.freshstore.catalog.dto.*;
import com.nikos.freshstore.catalog.service.StoreProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/store-products")
public class StoreProductController {

    private final StoreProductService storeProductService;

    public StoreProductController(StoreProductService storeProductService) {
        this.storeProductService = storeProductService;
    }

    @GetMapping("{storeId}/products")
    public List<StoreProductWithProductResponse> getStoreProducts(@PathVariable("storeId") UUID storeId) {
        return storeProductService.getStoreProducts(storeId);
    }

    @GetMapping("{productId}/stores")
    public List<StoreProductWithStoreResponse> getProductStores(@PathVariable("productId") UUID productId) {
        return storeProductService.getProductStores(productId);
    }

    @GetMapping("/{id}")
    public StoreProductResponse getStoreProduct(@PathVariable("id") UUID id) {
        return storeProductService.getStoreProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreProductResponse createProduct(@RequestBody CreateStoreProductRequest createStoreProductRequest) {
        return storeProductService.createStoreProduct(createStoreProductRequest);
    }

    @PutMapping("/{id}")
    public StoreProductResponse updateProduct(@RequestBody UpdateStoreProductRequest updateStoreProductRequest, UUID id) {
        return storeProductService.updateStoreProduct(updateStoreProductRequest, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStoreProduct(@PathVariable("id") UUID id) {
        storeProductService.deleteStoreProduct(id);
    }
}
