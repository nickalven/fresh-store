package com.nikos.freshstore.catalog.service;

import com.nikos.freshstore.catalog.domain.Category;
import com.nikos.freshstore.catalog.dto.CategoryResponse;
import com.nikos.freshstore.catalog.dto.CategoryRequest;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest category);

    List<CategoryResponse> findAllDescendantCategories(UUID parentId);

    List<CategoryResponse> findFullCategoriesTree();

    CategoryResponse findByCategoryId(UUID id);

    void deleteCategory(UUID uuid);

    List<CategoryResponse> getTopLevelCategories();

    CategoryResponse updateCategory(UUID id, CategoryRequest request);

}
