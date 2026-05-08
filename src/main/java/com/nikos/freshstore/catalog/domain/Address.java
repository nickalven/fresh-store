package com.nikos.freshstore.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Data
public class Address {

    @Column(name="street", nullable = false, length = 255)
    private String street;

    @Column(name="city", nullable = false, length = 100)
    private String city;

    @Column(name="region", length = 100)
    private String region;

    @Column(name="postcode", length = 20)
    private String postcode;

    @Column(name="country_code",nullable = false, length = 100)
    private String countryCode;
}
