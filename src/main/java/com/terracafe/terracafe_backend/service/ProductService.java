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
        // TODO: Add validation (e.g., name not empty, price > 0, category exists)
        validateCategory(product.getCategory());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Optional<Product> existingProductOpt = productRepository.findById(id);
        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            // TODO: Add validation for productDetails fields
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
        // TODO: Check if product is associated with any recipes or transaction items before deleting
        productRepository.deleteById(id);
    }

    // Metode bantuan untuk validasi kategori
    private void validateCategory(Category category) {
        if (category != null && category.getId() != null) {
            if (!categoryRepository.existsById(category.getId())) {
                throw new RuntimeException("Category not found with id: " + category.getId());
            }
        }
    }
}
