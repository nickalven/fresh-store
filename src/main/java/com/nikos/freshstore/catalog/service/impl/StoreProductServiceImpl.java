package com.nikos.freshstore.catalog.service.impl;

import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.domain.Store;
import com.nikos.freshstore.catalog.domain.StoreProduct;
import com.nikos.freshstore.catalog.dto.*;
import com.nikos.freshstore.catalog.exception.DuplicateResourceException;
import com.nikos.freshstore.catalog.exception.ResourceNotFoundException;
import com.nikos.freshstore.catalog.mapper.StoreProductMapper;
import com.nikos.freshstore.catalog.repository.ProductRepository;
import com.nikos.freshstore.catalog.repository.StoreProductRepository;
import com.nikos.freshstore.catalog.repository.StoreRepository;
import com.nikos.freshstore.catalog.service.StoreProductService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StoreProductServiceImpl implements StoreProductService {

    private final StoreProductRepository storeProductRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final StoreProductMapper storeProductMapper;


    public StoreProductServiceImpl(StoreProductRepository storeProductRepository,
                                   StoreRepository storeRepository,
                                   ProductRepository productRepository,
                                   StoreProductMapper storeProductMapper) {
        this.productRepository = productRepository;
        this.storeProductRepository = storeProductRepository;
        this.storeRepository = storeRepository;
        this.storeProductMapper = storeProductMapper;
    }

    @Transactional
    @Override
    public StoreProductResponse createStoreProduct(CreateStoreProductRequest createStoreProductRequest) {
        Store store = storeRepository.findById(createStoreProductRequest.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found: " + createStoreProductRequest.getStoreId()
                ));
        Product product = productRepository.findById(createStoreProductRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + createStoreProductRequest.getProductId()
                ));

        if (storeProductRepository.existsByStoreAndProduct(store, product)) {
            throw new DuplicateResourceException(
                    "Product '" + product.getName() + "' is already listed at " +
                    store.getName());
        }
        StoreProduct storeProduct = storeProductMapper.toEntity(createStoreProductRequest, store, product);

        try {
            return StoreProductResponse.from(storeProductRepository.save(storeProduct));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "This product is already listed at this store");
        }

    }

    @Override
    @Transactional
    public StoreProductResponse updateStoreProduct(UpdateStoreProductRequest request, UUID id) {

        StoreProduct storeProduct = storeProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StoreProduct not found: " + id));

        storeProductMapper.updateEntity(storeProduct, request);

        return StoreProductResponse.from(storeProductRepository.save(storeProduct));
    }

    @Transactional(readOnly = true)
    @Override
    public StoreProductResponse getStoreProduct(UUID id) {
        StoreProduct storeProduct = storeProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + id));

        return StoreProductResponse.from(storeProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StoreProductWithStoreResponse> getProductStores(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + productId
                ));

        return storeProductRepository.findByProductWithStore(product)
                .stream()
                .map(StoreProductWithStoreResponse::from)
                .toList();
    }


    @Transactional(readOnly = true)
    @Override
    public List<StoreProductWithProductResponse> getStoreProducts(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Store not found: " + storeId));

        return storeProductRepository.findByStoreWithProduct(store)
                .stream()
                .map(StoreProductWithProductResponse::from)
                .toList();
    }

    @Transactional
    @Override
    public void deleteStoreProduct(UUID id) {
        int deleted = storeProductRepository.deleteByIdReturningCount(id);
        if (deleted == 0) {
            throw new ResourceNotFoundException("StoreProduct not found: " + id);
        }
    }
}
