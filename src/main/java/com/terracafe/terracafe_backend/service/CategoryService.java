package com.terracafe.terracafe_backend.service;


import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.repository.CategoryRepository; // Pastikan repository ini ada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        // Add validation (e.g., ensure name is not empty)
        if (category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category) {
        // Untuk update, validasi hanya dilakukan jika category sudah memiliki ID
        // dan nama tidak null/blank (karena sudah ada data sebelumnya)
        if (category.getId() == null) {
            throw new IllegalArgumentException("Category ID is required for update");
        }
        if (category.getName() == null || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        // Check if category is associated with any products before deleting
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                throw new RuntimeException("Cannot delete category '" + category.getName() + "' because it has " + 
                                         category.getProducts().size() + " associated product(s). Please delete or reassign the products first.");
            }
            categoryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }
}
