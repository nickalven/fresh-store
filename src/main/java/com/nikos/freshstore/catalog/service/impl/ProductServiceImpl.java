package com.nikos.freshstore.catalog.service.impl;

import com.nikos.freshstore.catalog.domain.Category;
import com.nikos.freshstore.catalog.domain.Product;
import com.nikos.freshstore.catalog.dto.ProductRequest;
import com.nikos.freshstore.catalog.dto.ProductResponse;
import com.nikos.freshstore.catalog.dto.ProductSummaryResponse;
import com.nikos.freshstore.catalog.exception.DuplicateResourceException;
import com.nikos.freshstore.catalog.exception.ResourceNotFoundException;
import com.nikos.freshstore.catalog.mapper.ProductMapper;
import com.nikos.freshstore.catalog.repository.CategoryRepository;
import com.nikos.freshstore.catalog.repository.ProductRepository;
import com.nikos.freshstore.catalog.service.ProductService;
import com.nikos.freshstore.catalog.utils.SlugUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }


    @Transactional(readOnly = true)
    @Override
    public List<ProductSummaryResponse> getSummaryProducts() {
        return productRepository.findAllActiveWithPrimaryImage().stream()
                .map(ProductSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + id));;

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getProductsByCategory(UUID categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + categoryId));

        List<UUID> productIds = productRepository
                .findProductIdsByCategoryAndDescendants(categoryId);

        if (productIds.isEmpty()) {
            return List.of();
        }

        return productRepository.findByIdInWithImages(productIds)
                .stream()
                .map(ProductSummaryResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        checkDuplicateBarcode(request.getBarcode(), null);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product category not found: " + request.getCategoryId()
                ));
        Product product = productMapper.toEntity(request, category);
        product.setSlug(resolveSlugCollision(
                SlugUtils.slugify(request.getName())));

        try {
            return ProductResponse.from(productRepository.save(product));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "A product with this barcode or slug already exists");
        }
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(ProductRequest request, UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + id));

        checkDuplicateBarcode(request.getBarcode(), id);

        if (!product.getName().equalsIgnoreCase(request.getName())) {
            product.setSlug(resolveSlugCollision(
                    SlugUtils.slugify(request.getName())));
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + request.getCategoryId()));

        productMapper.updateEntity(product, request, category);


        try {
            return ProductResponse.from(productRepository.save(product));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "A product with this barcode or slug already exists");
        }
    }


    @Transactional
    public void deleteProduct(UUID id) {
        int updated = productRepository.deactivateById(id);
        if (updated == 0) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
    }

    private void checkDuplicateBarcode(String barcode, UUID excludeId) {
        if (barcode == null) return;

        boolean duplicate = (excludeId == null)
                ? productRepository.existsByBarcode(barcode)
                : productRepository.existsByBarcodeAndIdNot(barcode, excludeId);

        if (duplicate) {
            throw new DuplicateResourceException(
                    "A product with barcode '" + barcode + "' already exists");
        }
    }

    private String resolveSlugCollision(String slug) {
        if (!productRepository.existsBySlug(slug)) {
            return slug;
        }
        return slug + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
