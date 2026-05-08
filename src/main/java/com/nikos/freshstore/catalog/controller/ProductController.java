package com.nikos.freshstore.catalog.controller;

import com.nikos.freshstore.catalog.dto.ProductRequest;
import com.nikos.freshstore.catalog.dto.ProductResponse;
import com.nikos.freshstore.catalog.dto.ProductSummaryResponse;
import com.nikos.freshstore.catalog.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductSummaryResponse> getSummaryProducts() {
        return productService.getSummaryProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable("id") UUID id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductSummaryResponse> getProductsByCategory(
            @PathVariable UUID categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@RequestBody ProductRequest productRequest, @PathVariable("id") UUID id) {
        return productService.updateProduct(productRequest, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteProduct(id);
    }


}
