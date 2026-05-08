package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Store;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class StoreResponse {
    private UUID id;
    private String name;
    private String slug;
    private AddressResponse address;
    private BigDecimal lat;
    private BigDecimal lng;
    private boolean isActive;

    public static StoreResponse from(Store store) {
        StoreResponse response = new StoreResponse();
        response.setId(store.getId());
        response.setName(store.getName());
        response.setSlug(store.getSlug());
        response.setLat(store.getLat());
        response.setLng(store.getLng());
        response.setActive(store.isActive());

        if (store.getAddress() != null) {
            AddressResponse address = new AddressResponse();
            address.setStreet(store.getAddress().getStreet());
            address.setCity(store.getAddress().getCity());
            address.setRegion(store.getAddress().getRegion());
            address.setPostcode(store.getAddress().getPostcode());
            address.setCountryCode(store.getAddress().getCountryCode());
            response.setAddress(address);
        }

        return response;
    }
}
