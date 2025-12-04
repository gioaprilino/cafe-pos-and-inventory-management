package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Recipe;
import com.terracafe.terracafe_backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes") // Base path untuk endpoint recipe
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Optional<Recipe> recipe = recipeService.getRecipeById(id);
        return recipe.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<Recipe> getRecipesByProductId(@PathVariable Long productId) {
        return recipeService.getRecipesByProductId(productId);
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<Recipe> getRecipesByIngredientId(@PathVariable Long ingredientId) {
        return recipeService.getRecipesByIngredientId(ingredientId);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
        try {
            Recipe savedRecipe = recipeService.saveRecipe(recipe);
            return ResponseEntity.ok(savedRecipe);
        } catch (Exception e) {
            // Handle potential errors (e.g., product or ingredient not found)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @Valid @RequestBody Recipe recipeDetails) {
        Optional<Recipe> existingRecipeOpt = recipeService.getRecipeById(id);
        if (existingRecipeOpt.isPresent()) {
            Recipe existingRecipe = existingRecipeOpt.get();
            existingRecipe.setProduct(recipeDetails.getProduct());
            existingRecipe.setIngredient(recipeDetails.getIngredient());
            existingRecipe.setQuantityNeeded(recipeDetails.getQuantityNeeded());
            // createdAt tidak diupdate
            try {
                Recipe updatedRecipe = recipeService.updateRecipe(id, existingRecipe); // Pass id explicitly
                return ResponseEntity.ok(updatedRecipe);
            } catch (Exception e) {
                // Handle potential errors (e.g., product or ingredient not found)
                return ResponseEntity.badRequest().body(null); // Or return error details
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.ok("Recipe deleted successfully");
    }
}
