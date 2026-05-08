package com.nikos.freshstore.catalog.service.impl;

import com.nikos.freshstore.catalog.dto.CategoryResponse;
import com.nikos.freshstore.catalog.dto.CategoryRequest;
import com.nikos.freshstore.catalog.exception.BusinessRuleException;
import com.nikos.freshstore.catalog.exception.DuplicateResourceException;
import com.nikos.freshstore.catalog.exception.ResourceNotFoundException;
import com.nikos.freshstore.catalog.repository.CategoryRepository;
import com.nikos.freshstore.catalog.domain.Category;
import com.nikos.freshstore.catalog.service.CategoryService;
import com.nikos.freshstore.catalog.utils.SlugUtils;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category " + request.getParentId() + " not found"));

        }

        boolean duplicate = (parent == null) ?
                categoryRepository.existsByNameIgnoreCaseAndParentIsNull(request.getName()) :
                categoryRepository.existsByNameIgnoreCaseAndParent(request.getName(), parent);

        if (duplicate) {
            throw new DuplicateResourceException(
                    "Category name '" + request.getName() + "' already exists" +
                            ((parent == null) ? " at the top level" : " under category " + parent.getName())
            );
        }

        String slug = resolveSlugCollision(SlugUtils.slugify(request.getName()));


        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setParent(parent);
        category.setSortOrder(request.getSortOrder());
        try {
            return CategoryResponse.from(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(
                    "A category with this name already exists under the same parent"
            );
        }
    }


    @Override
    public List<CategoryResponse> findFullCategoriesTree() {
         List<Category> allCategories =  categoryRepository.findFullTree();

        Map<UUID, CategoryResponse> responseMap = allCategories
                .stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        CategoryResponse::from)
                );
        List<CategoryResponse> root = new ArrayList<>();
        for (Category category: allCategories) {
            CategoryResponse categoryResponse = responseMap.get(category.getId());

            if (category.getParent() == null) {
                root.add(categoryResponse);
            } else {
                CategoryResponse parent = responseMap.get(category.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(categoryResponse);
                }
            }
        }

        return root;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> getTopLevelCategories() {
        return categoryRepository.findByParentIsNullOrderBySortOrderAscNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> findAllDescendantCategories(UUID parentId) {
        categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category " + parentId + " not found"));
        return categoryRepository.findAllDescendants(parentId)
                .stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Override
    public CategoryResponse findByCategoryId(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category with id " + id + " not found"
                ));
        return CategoryResponse.from(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found: " + id));

        if (request.getParentId() != null && request.getParentId().equals(id)) {
            throw new BusinessRuleException(
                    "A category cannot be its own parent");
        }

        checkForCircularRefernce(id, request);

        Category newParent = null;
        if (request.getParentId() != null) {
            newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found: " + request.getParentId()));
        }

        boolean duplicate = (newParent == null)
                ? categoryRepository.existsByNameIgnoreCaseAndParentIsNullAndIdNot(
                request.getName(), id)
                : categoryRepository.existsByNameIgnoreCaseAndParentAndIdNot(
                request.getName(), newParent, id);

        if (duplicate) {
            throw new DuplicateResourceException(
                    "A category named '" + request.getName() + "' already exists " +
                            (newParent == null ? "at the top level"
                                    : "under '" + newParent.getName() + "'"));
        }

        if (!category.getName().equalsIgnoreCase(request.getName())) {
            category.setSlug(resolveSlugCollision(
                    SlugUtils.slugify(request.getName())));
        }

        category.setName(request.getName());
        category.setParent(newParent);
        category.setSortOrder(request.getSortOrder());

        return CategoryResponse.from(categoryRepository.save(category));
    }

    private void checkForCircularRefernce(UUID id, CategoryRequest request) {
        if (request.getParentId() != null) {
            List<Category> descendants = categoryRepository.findAllDescendants(id);
            boolean circularReference = descendants.stream()
                    .anyMatch(d -> d.getId().equals(request.getParentId()));
            if (circularReference) {
                throw new BusinessRuleException(
                        "Cannot set a descendant category as parent — " +
                                "this would create a circular reference");
            }
        }
    }


    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        if (categoryRepository.existsByParentId(id)) {
            throw new BusinessRuleException(
                    "Cannot delete category that has subcategories");
        }
        int deleted = categoryRepository.deleteByIdReturningCount(id);
        if (deleted == 0) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
    }

    private String resolveSlugCollision(String slug) {
        if (!categoryRepository.existsBySlug(slug)) {
            return slug;
        }
        return slug + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

}
