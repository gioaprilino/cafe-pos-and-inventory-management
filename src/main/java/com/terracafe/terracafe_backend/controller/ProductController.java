package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        try {
            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            // Handle potential errors (e.g., category not found)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        Optional<Product> existingProductOpt = productService.getProductById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            // Update fields based on productDetails
            existingProduct.setName(productDetails.getName());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setImageUrl(productDetails.getImageUrl());
            existingProduct.setIsActive(productDetails.getIsActive());
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
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        // TODO: Add authorization check (Manager only)
        // TODO: Check if product is associated with recipes or transaction items before deletion
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if product is associated
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
