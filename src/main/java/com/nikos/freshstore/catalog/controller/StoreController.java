package com.nikos.freshstore.catalog.controller;

import com.nikos.freshstore.catalog.dto.StoreRequest;
import com.nikos.freshstore.catalog.dto.StoreResponse;
import com.nikos.freshstore.catalog.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<StoreResponse> getAllStores() {
        return storeService.getAllStores();
    }

    @GetMapping("/{id}")
    public StoreResponse getStore(@PathVariable("id") UUID id) {
        return storeService.getStoreById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponse createStore(@RequestBody StoreRequest request) {
        return storeService.createStore(request);
    }

    @PutMapping("/{id}")
    public StoreResponse updateStore(@PathVariable("id") UUID id, @RequestBody StoreRequest request) {
        return storeService.updateStore(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable("id") UUID id) {
        storeService.deleteStore(id);
    }
}
