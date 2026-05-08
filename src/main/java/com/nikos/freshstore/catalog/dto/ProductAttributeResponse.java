package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.ProductAttribute;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductAttributeResponse {
    private UUID id;
    private String attrKey;
    private String attrValue;

    public static ProductAttributeResponse from(ProductAttribute productAttribute) {
        ProductAttributeResponse response = new ProductAttributeResponse();
        response.setId(productAttribute.getId());
        response.setAttrKey(productAttribute.getAttrKey());
        response.setAttrValue(productAttribute.getAttrValue());
        return response;
    }

}

