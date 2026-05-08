package com.nikos.freshstore.catalog.controller;

import com.nikos.freshstore.catalog.dto.CategoryRequest;
import com.nikos.freshstore.catalog.dto.CategoryResponse;
import com.nikos.freshstore.catalog.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {


    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> fullCategoriesTree() {
        return categoryService.findFullCategoriesTree();
    }

    @GetMapping("/top")
    public List<CategoryResponse> findTopCategories(@PathVariable("id") UUID id) {
        return categoryService.getTopLevelCategories();
    }

    @GetMapping("/{id}/descendants")
    public List<CategoryResponse> findDescendants(@PathVariable("id") UUID id) {
        return categoryService.findAllDescendantCategories(id);
    }

    @GetMapping("/{id}")
    public CategoryResponse findCategory(@PathVariable("id") UUID id) {
        return categoryService.findByCategoryId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    public CategoryResponse createCategory(@PathVariable("id") UUID id, @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") UUID id){
        categoryService.deleteCategory(id);
    }
}
