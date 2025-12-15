package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        // Authorization check (Manager only)
        // Validation (@Valid) is applied via @Valid annotation above
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Handle potential errors
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        // Authorization check (Manager only)
        // Tidak menggunakan @Valid untuk mendukung partial update
        Optional<Category> existingCategoryOpt = categoryService.getCategoryById(id);
        if (existingCategoryOpt.isPresent()) {
            Category existingCategory = existingCategoryOpt.get();
            
            // Update hanya field yang tidak null dan tidak blank
            if (categoryDetails.getName() != null) {
                if (categoryDetails.getName().isBlank()) {
                    return ResponseEntity.badRequest().build(); // Reject empty name
                }
                existingCategory.setName(categoryDetails.getName());
            }
            if (categoryDetails.getDescription() != null) {
                existingCategory.setDescription(categoryDetails.getDescription());
            }
            // createdAt tidak diupdate
            
            try {
                Category updatedCategory = categoryService.updateCategory(existingCategory);
                return ResponseEntity.ok(updatedCategory);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            } catch (Exception e) {
                // Handle potential errors
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        // Authorization check (Manager only)
        // Check if category is associated with products before deletion
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if category is associated with products
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
