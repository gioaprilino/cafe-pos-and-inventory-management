package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.repository.CategoryRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.ProductRepository; // Pastikan repository ini ada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository; // Untuk validasi kategori

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        // Add validation (e.g., name not empty, price > 0, category exists)
        validateProductFields(product);
        validateCategory(product.getCategory());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            // Add validation for productDetails fields
            validateProductFields(productDetails);
            validateCategory(productDetails.getCategory()); // Validasi kategori baru jika diubah

            existingProduct.setName(productDetails.getName());
            existingProduct.setCategory(productDetails.getCategory());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setImageUrl(productDetails.getImageUrl());
            existingProduct.setIsActive(productDetails.getIsActive());
            // createdAt tidak diupdate
            return productRepository.save(existingProduct);
        } else {
            // Handle not found, throw exception or return null
            throw new RuntimeException("Product not found with id: " + id); // Or throw custom exception
        }
    }

    public void deleteProduct(Long id) {
        // Check if product is associated with any recipes or transaction items before deleting
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            // Note: In a production system, you should check if product is used in recipes or transactions
            // For now, we'll allow deletion - implement proper cascade rules if needed
            productRepository.deleteById(id);
        } else {
            throw new RuntimeException("Product not found with id: " + id);
        }
    }

    // Metode bantuan untuk validasi kategori
    private void validateCategory(Category category) {
        if (category != null && category.getId() != null) {
            if (!categoryRepository.existsById(category.getId())) {
                throw new RuntimeException("Category not found with id: " + category.getId());
            }
        } else {
            throw new RuntimeException("Category is required for product");
        }
    }

    // Metode bantuan untuk validasi field produk
    private void validateProductFields(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
    }
}
