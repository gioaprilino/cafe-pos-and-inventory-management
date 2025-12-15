package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products") // Base path untuk endpoint product
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/active") // Endpoint khusus untuk produk aktif
    public List<Product> getActiveProducts() {
        return productService.getActiveProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            // Handle potential errors (e.g., category not found)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Product> existingProductOpt = productService.getProductById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            
            // Update hanya field yang tidak null
            if (productDetails.getName() != null) {
                if (productDetails.getName().isBlank()) {
                    return ResponseEntity.badRequest().build(); // Reject empty name
                }
                existingProduct.setName(productDetails.getName());
            }
            if (productDetails.getCategory() != null) {
                existingProduct.setCategory(productDetails.getCategory());
            }
            if (productDetails.getPrice() != null) {
                if (productDetails.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
                    return ResponseEntity.badRequest().build(); // Reject negative price
                }
                existingProduct.setPrice(productDetails.getPrice());
            }
            if (productDetails.getDescription() != null) {
                existingProduct.setDescription(productDetails.getDescription());
            }
            if (productDetails.getImageUrl() != null) {
                existingProduct.setImageUrl(productDetails.getImageUrl());
            }
            if (productDetails.getIsActive() != null) {
                existingProduct.setIsActive(productDetails.getIsActive());
            }
            // createdAt tidak diupdate
            
            try {
                Product updatedProduct = productService.updateProduct(id, existingProduct); // Pass id explicitly
                return ResponseEntity.ok(updatedProduct);
            } catch (Exception e) {
                // Handle potential errors (e.g., category not found)
                return ResponseEntity.badRequest().body(null); // Or return error details
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if product is associated
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
