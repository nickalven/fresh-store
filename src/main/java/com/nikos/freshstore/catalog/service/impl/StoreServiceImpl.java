package com.nikos.freshstore.catalog.service.impl;

import com.nikos.freshstore.catalog.domain.Address;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.dto.CategoryResponse;
import com.nikos.freshstore.catalog.dto.StoreRequest;
import com.nikos.freshstore.catalog.dto.StoreResponse;
import com.nikos.freshstore.catalog.exception.DuplicateResourceException;
import com.nikos.freshstore.catalog.exception.ResourceNotFoundException;
import com.nikos.freshstore.catalog.mapper.StoreMapper;
import com.nikos.freshstore.catalog.repository.StoreRepository;
import com.nikos.freshstore.catalog.service.StoreService;
import com.nikos.freshstore.catalog.utils.SlugUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreRepository storeRepository, StoreMapper storeMapper) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
    }

    @Override
    public List<StoreResponse> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(StoreResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public StoreResponse getStoreById(UUID id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store with id " + id + " not found"
                ));
        return StoreResponse.from(store);
    }

    @Transactional
    @Override
    public StoreResponse createStore(StoreRequest request) {
        checkDuplicateName(request.getName(), null);
        Store store = storeMapper.toEntity(request);
        store.setSlug(resolveSlugCollision(
                SlugUtils.slugify(request.getName())));
        try {
            return StoreResponse.from(storeRepository.save(store));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "A store with this name already exists"
            );
        }
    }


    @Transactional
    @Override
    public StoreResponse updateStore(UUID id, StoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found: " + id));

        checkDuplicateName(request.getName(), id);

        if (!store.getName().equalsIgnoreCase(request.getName())) {
            store.setSlug(resolveSlugCollision(
                    SlugUtils.slugify(request.getName())));
        }

        storeMapper.updateEntity(store, request);
        return StoreResponse.from(storeRepository.save(store));
    }


    @Transactional
    @Override
    public void deleteStore(UUID id) {
        int deleted = storeRepository.deleteByIdReturningCount(id);

        if (deleted == 0) {
            throw new ResourceNotFoundException("Store not found: " + id);
        }
    }

    private String resolveSlugCollision(String slug) {
        if (!storeRepository.existsBySlug(slug)) {
            return slug;
        }
        return slug + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }


    private void checkDuplicateName(String name, UUID id) {
        boolean duplicate = (id == null)
                ? storeRepository.existsByNameIgnoreCase(name)
                : storeRepository.existsByNameIgnoreCaseAndIdNot(name, id);

        if (duplicate) {
            throw new DuplicateResourceException(
                    "A store named '" + name + "' already exists");
        }
    }

}
