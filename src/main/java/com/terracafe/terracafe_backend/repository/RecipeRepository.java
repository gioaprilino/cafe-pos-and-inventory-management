package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // Temukan resep berdasarkan ID produk
    List<Recipe> findByProductId(Long productId);

    // Temukan resep berdasarkan ID bahan
    List<Recipe> findByIngredientId(Long ingredientId);

    // Temukan satu resep berdasarkan ID produk dan ID bahan (untuk validasi atau update)
    Optional<Recipe> findByProductIdAndIngredientId(Long productId, Long ingredientId);
}
