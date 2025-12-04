package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.Ingredient;
import com.terracafe.terracafe_backend.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredients") // Base path untuk endpoint ingredient
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Optional<Ingredient> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint untuk mendapatkan stok saat ini dari suatu bahan
    @GetMapping("/{id}/stock")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
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
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public ResponseEntity<Boolean> isLowStock(@PathVariable Long id) {
        try {
            boolean isLow = ingredientService.isLowStock(id);
            return ResponseEntity.ok(isLow);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Or handle differently
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Ingredient> createIngredient(@Valid @RequestBody Ingredient ingredient) {
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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Ingredient> updateIngredient(@PathVariable Long id, @Valid @RequestBody Ingredient ingredientDetails) {
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
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> deleteIngredient(@PathVariable Long id) {
        try {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.ok("Ingredient deleted successfully");
        } catch (RuntimeException e) {
            // Handle error if ingredient is associated
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
