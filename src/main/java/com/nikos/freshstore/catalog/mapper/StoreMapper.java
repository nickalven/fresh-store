package com.nikos.freshstore.catalog.mapper;

import com.nikos.freshstore.catalog.domain.Address;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.dto.AddressRequest;
import com.nikos.freshstore.catalog.dto.StoreRequest;
import com.nikos.freshstore.catalog.utils.SlugUtils;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public Store toEntity(StoreRequest request) {
        Store store = new Store();
        store.setName(request.getName());
        store.setLat(request.getLat());
        store.setLng(request.getLng());
        store.setActive(true);

        if (request.getAddress() != null) {
            store.setAddress(toAddress(request.getAddress()));
        }

        return store;
    }

    public void updateEntity(Store store, StoreRequest request) {
        store.setName(request.getName());
        store.setLat(request.getLat());
        store.setLng(request.getLng());

        if (request.getAddress() != null) {
            store.setAddress(toAddress(request.getAddress()));
        }
    }

    private Address toAddress(AddressRequest request) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setRegion(request.getRegion());
        address.setPostcode(request.getPostcode());
        address.setCountryCode(request.getCountryCode());
        return address;
    }
}