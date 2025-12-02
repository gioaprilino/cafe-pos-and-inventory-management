package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Ingredient;
import com.terracafe.terracafe_backend.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredients") // Base path untuk endpoint ingredient
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint untuk mendapatkan stok saat ini dari suatu bahan
    @GetMapping("/{id}/stock")
    public ResponseEntity<Integer> getCurrentStock(@PathVariable Long id) {
        try {
            int currentStock = ingredientService.getCurrentStock(id);
            return ResponseEntity.ok(currentStock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Or handle differently
        }
    }

    // Endpoint untuk mengecek apakah stok rendah
    @GetMapping("/{id}/low-stock")
    public ResponseEntity<Boolean> isLowStock(@PathVariable Long id) {
        try {
            boolean isLow = ingredientService.isLowStock(id);
            return ResponseEntity.ok(isLow);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Or handle differently
        }
    }

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        try {
            Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
            return ResponseEntity.ok(savedIngredient);
        } catch (IllegalArgumentException e) {
             // Handle validation error like minimumStockThreshold < 0
             return ResponseEntity.badRequest().body(null); // Or return error details
        } catch (Exception e) {
            // Handle other potential errors
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody Ingredient ingredientDetails) {
        // TODO: Add authorization check (Manager only)
        // TODO: Add validation (@Valid)
        Optional<Ingredient> existingIngredientOpt = ingredientService.getIngredientById(id);
        if (existingIngredientOpt.isPresent()) {
            Ingredient existingIngredient = existingIngredientOpt.get();
            existingIngredient.setName(ingredientDetails.getName());
            existingIngredient.setUnit(ingredientDetails.getUnit());
            existingIngredient.setMinimumStockThreshold(ingredientDetails.getMinimumStockThreshold());
            // createdAt tidak diupdate
            try {
                Ingredient updatedIngredient = ingredientService.updateIngredient(id, existingIngredient); // Pass id explicitly
                return ResponseEntity.ok(updatedIngredient);
            } catch (IllegalArgumentException e) {
                 // Handle validation error like minimumStockThreshold < 0
                 return ResponseEntity.badRequest().body(null); // Or return error details
            } catch (Exception e) {
                // Handle other potential errors
                return ResponseEntity.badRequest().body(null); // Or return error details
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIngredient(@PathVariable Long id) {
        // TODO: Add authorization check (Manager only)
        // TODO: Check if ingredient is associated with recipes before deletion
        try {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.ok("Ingredient deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if ingredient is associated
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
