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
        // TODO: Add validation (e.g., ensure name is not empty)
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        // TODO: Check if category is associated with any products before deleting
        categoryRepository.deleteById(id);
    }
}
