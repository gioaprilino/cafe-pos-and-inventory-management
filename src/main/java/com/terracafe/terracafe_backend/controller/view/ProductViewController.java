package com.terracafe.terracafe_backend.controller.view;

import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.service.CategoryService;
import com.terracafe.terracafe_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductViewController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("title", "Product Management");
        return "products/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('MANAGER')")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Add New Product");
        return "products/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id)));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("title", "Edit Product");
        return "products/form";
    }

    // We will handle the actual creation/update via the API or a new separate
    // method here.
    // For simplicity, let's use the API endpoints from the form via JavaScript or
    // implement standard form submission handlers here if we want a pure SSR
    // approach.
    // Let's implement standard form submission here for a "pure" Thymeleaf
    // experience
    // alongside the REST API.

    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveProduct(@ModelAttribute Product product) {
        if (product.getId() == null) {
            productService.saveProduct(product);
        } else {
            // We need to fetch the existing one to preserve fields if not all are in the
            // form,
            // or just update what we have. Ideally we'd validte here.
            // For now, assuming the Service handles update logic or we use the simple save
            // for overwrite if not careful.
            // ProductService.updateProduct takes ID and object.
            productService.updateProduct(product.getId(), product);
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
