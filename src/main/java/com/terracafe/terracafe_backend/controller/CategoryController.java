package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories") // Base path untuk endpoint category
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(savedCategory);
        } catch (Exception e) {
            // Handle potential errors
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            existingCategory.setName(categoryDetails.getName());
            existingCategory.setDescription(categoryDetails.getDescription());
            // createdAt tidak diupdate
            try {
                Category updatedCategory = categoryService.saveCategory(existingCategory);
                return ResponseEntity.ok(updatedCategory);
            } catch (Exception e) {
                // Handle potential errors
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // TODO: Add authorization check (Manager only)
        // TODO: Check if category is associated with products before deletion
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if category is associated with products
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
