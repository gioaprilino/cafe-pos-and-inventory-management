package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue(); // Ambil produk yang aktif
    List<Product> findByCategory_Id(Long categoryId); // Ambil produk berdasarkan kategori
}
