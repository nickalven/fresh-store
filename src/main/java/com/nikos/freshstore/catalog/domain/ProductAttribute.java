package com.nikos.freshstore.catalog.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "product_attribute", schema = "catalog")
public class ProductAttribute {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "attr_key", nullable = false, length = 50)
    private String attrKey;

    @Column(name = "attr_value", length = 255)
    private String attrValue;
}