package com.terracafe.terracafe_backend.service;


import com.terracafe.terracafe_backend.model.Ingredient;
import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.model.Recipe;
import com.terracafe.terracafe_backend.repository.IngredientRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.ProductRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.RecipeRepository; // Pastikan repository ini ada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ProductRepository productRepository; // Untuk validasi produk
    @Autowired
    private IngredientRepository ingredientRepository; // Untuk validasi bahan

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public List<Recipe> getRecipesByProductId(Long productId) {
        return recipeRepository.findByProductId(productId);
    }

    public List<Recipe> getRecipesByIngredientId(Long ingredientId) {
        return recipeRepository.findByIngredientId(ingredientId);
    }

    public Recipe saveRecipe(Recipe recipe) {
        // Add validation (e.g., product and ingredient exist, quantityNeeded > 0)
        validateProductAndIngredient(recipe.getProduct(), recipe.getIngredient());
        validateQuantity(recipe.getQuantityNeeded());
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, Recipe recipeDetails) {
        Optional<Recipe> existingRecipeOpt = recipeRepository.findById(id);
        if (existingRecipeOpt.isPresent()) {
            Recipe existingRecipe = existingRecipeOpt.get();
            // Add validation for recipeDetails fields
            validateProductAndIngredient(recipeDetails.getProduct(), recipeDetails.getIngredient());
            validateQuantity(recipeDetails.getQuantityNeeded());

            existingRecipe.setProduct(recipeDetails.getProduct());
            existingRecipe.setIngredient(recipeDetails.getIngredient());
            existingRecipe.setQuantityNeeded(recipeDetails.getQuantityNeeded());
            // createdAt tidak diupdate
            return recipeRepository.save(existingRecipe);
        } else {
            throw new RuntimeException("Recipe not found with id: " + id); // Or throw custom exception
        }
    }

    public void deleteRecipe(Long id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            recipeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Recipe not found with id: " + id);
        }
    }

    // Metode bantuan untuk validasi produk dan bahan
    private void validateProductAndIngredient(Product product, Ingredient ingredient) {
        if (product != null && product.getId() != null) {
            if (!productRepository.existsById(product.getId())) {
                throw new RuntimeException("Product not found with id: " + product.getId());
            }
        } else {
            throw new RuntimeException("Product is required for recipe");
        }
        if (ingredient != null && ingredient.getId() != null) {
            if (!ingredientRepository.existsById(ingredient.getId())) {
                throw new RuntimeException("Ingredient not found with id: " + ingredient.getId());
            }
        } else {
            throw new RuntimeException("Ingredient is required for recipe");
        }
    }

    // Metode bantuan untuk validasi kuantitas
    private void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity needed must be greater than 0");
        }
    }
}
