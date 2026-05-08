package com.nikos.freshstore.catalog.dto;

import lombok.Data;

@Data
public class AddressResponse {
    private String street;
    private String city;
    private String region;
    private String postcode;
    private String countryCode;
}