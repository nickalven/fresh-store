package com.nikos.freshstore.catalog.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "store", schema = "catalog")
public class Store {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="name", nullable = false, length = 150)
    private String name;

    @Column(name="slug", nullable = false, length = 150, unique = true)
    private String slug;

    @Embedded
    private Address address;

    @Column(name = "lat", nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(name = "lng", nullable = false, precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;


}
