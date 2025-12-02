package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Contoh method tambahan jika diperlukan:
    List<Ingredient> findByMinimumStockThresholdGreaterThan(Integer threshold);
    List<Ingredient> findByNameContainingIgnoreCase(String name);
}
