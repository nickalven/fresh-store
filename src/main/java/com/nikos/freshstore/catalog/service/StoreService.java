package com.nikos.freshstore.catalog.service;

import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.dto.StoreRequest;
import com.nikos.freshstore.catalog.dto.StoreResponse;

import java.util.List;
import java.util.UUID;

public interface StoreService {

    List<StoreResponse> getAllStores();

    StoreResponse getStoreById(UUID id);

    StoreResponse createStore(StoreRequest request);

    StoreResponse updateStore(UUID id, StoreRequest request);

    void deleteStore(UUID id);
}
