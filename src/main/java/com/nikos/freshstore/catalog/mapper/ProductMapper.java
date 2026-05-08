package com.nikos.freshstore.catalog.mapper;

import com.nikos.freshstore.catalog.domain.*;
import com.nikos.freshstore.catalog.dto.ProductAttributeRequest;
import com.nikos.freshstore.catalog.dto.ProductImageRequest;
import com.nikos.freshstore.catalog.dto.ProductRequest;
import com.nikos.freshstore.catalog.dto.StoreRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest productRequest, Category category) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setBarcode(productRequest.getBarcode());
        product.setUnitType(productRequest.getUnitType());
        product.setUnitSize(productRequest.getUnitSize());
        product.setCategory(category);
        product.setActive(true);
        productRequest.getAttributes().stream()
                .map((this::toProductAttributeEntity))
                .forEach(product.getAttributes()::add);
        productRequest.getImages().stream()
                .map(this::toProductImageEntity)
                .forEach(product.getImages()::add);
        return product;
    }

    public void updateEntity(Product product, ProductRequest productRequest, Category category) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setBarcode(productRequest.getBarcode());
        product.setUnitType(productRequest.getUnitType());
        product.setUnitSize(productRequest.getUnitSize());
        product.setCategory(category);
        productRequest.getAttributes().clear();
        productRequest.getImages().clear();
        productRequest.getAttributes().stream()
                .map((this::toProductAttributeEntity))
                .forEach(product.getAttributes()::add);
        productRequest.getImages().stream()
                .map(this::toProductImageEntity)
                .forEach(product.getImages()::add);

    }


    public ProductAttribute toProductAttributeEntity(ProductAttributeRequest productAttributeRequest) {
        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setAttrKey(productAttributeRequest.getAttrKey());
        productAttribute.setAttrValue(productAttributeRequest.getAttrValue());
        return productAttribute;
    }

    public ProductImage toProductImageEntity(ProductImageRequest productImageRequest) {
        ProductImage productImage = new ProductImage();
        productImage.setUrl(productImageRequest.getUrl());
        productImage.setAltText(productImageRequest.getAltText());
        productImage.setPrimary(productImageRequest.isPrimary());
        productImage.setSortOrder(productImageRequest.getSortOrder());
        return productImage;
    }
}
