package com.terracafe.terracafe_backend.controller.view;

import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Category Management");
        return "categories/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('MANAGER')")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("title", "Add New Category");
        return "categories/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id)));
        model.addAttribute("title", "Edit Category");
        return "categories/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveCategory(@ModelAttribute Category category) {
        if (category.getId() == null) {
            categoryService.saveCategory(category);
        } else {
            // Assuming update logic similar to saving if ID is present or handled by
            // service
            categoryService.updateCategory(category);
        }
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
