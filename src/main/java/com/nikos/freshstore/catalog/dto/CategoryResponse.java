package com.nikos.freshstore.catalog.dto;

import com.nikos.freshstore.catalog.domain.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID parentId;
    private int sortOrder;
    private List<CategoryResponse> children = new ArrayList<>();

    public static CategoryResponse from(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setParentId(
                category.getParent() != null ? category.getParent().getId() : null
        );
        response.setSortOrder(category.getSortOrder());
        return response;
    }
}