package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.Ingredient;
import com.terracafe.terracafe_backend.repository.IngredientRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.StockMovementRepository; // Untuk menghitung stok
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository; // Untuk menghitung stok

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        // Add validation (e.g., name and unit not empty, minimumStockThreshold >= 0)
        if (ingredient.getName() == null || ingredient.getName().isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be empty");
        }
        if (ingredient.getUnit() == null || ingredient.getUnit().isBlank()) {
            throw new IllegalArgumentException("Ingredient unit cannot be empty");
        }
        // Validasi minimumStockThreshold
        if (ingredient.getMinimumStockThreshold() == null || ingredient.getMinimumStockThreshold() < 0) {
             throw new IllegalArgumentException("Minimum stock threshold must be 0 or greater.");
        }
        return ingredientRepository.save(ingredient);
    }

    public Ingredient updateIngredient(Long id, Ingredient ingredientDetails) {
        Optional<Ingredient> existingIngredientOpt = ingredientRepository.findById(id);
        if (existingIngredientOpt.isPresent()) {
            Ingredient existingIngredient = existingIngredientOpt.get();
            // Add validation for ingredientDetails fields
            if (ingredientDetails.getName() != null && !ingredientDetails.getName().isBlank()) {
                existingIngredient.setName(ingredientDetails.getName());
            }
            if (ingredientDetails.getUnit() != null && !ingredientDetails.getUnit().isBlank()) {
                existingIngredient.setUnit(ingredientDetails.getUnit());
            }
            if (ingredientDetails.getMinimumStockThreshold() != null) {
                if (ingredientDetails.getMinimumStockThreshold() < 0) {
                    throw new IllegalArgumentException("Minimum stock threshold must be 0 or greater.");
                }
                existingIngredient.setMinimumStockThreshold(ingredientDetails.getMinimumStockThreshold());
            }
            // createdAt tidak diupdate
            return ingredientRepository.save(existingIngredient);
        } else {
            throw new RuntimeException("Ingredient not found with id: " + id); // Or throw custom exception
        }
    }

    public void deleteIngredient(Long id) {
        // Check if ingredient is associated with any recipes before deleting
        Optional<Ingredient> ingredientOpt = ingredientRepository.findById(id);
        if (ingredientOpt.isPresent()) {
            // Note: In production, implement proper check for recipe associations
            ingredientRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ingredient not found with id: " + id);
        }
    }

    // Metode untuk menghitung stok saat ini berdasarkan pergerakan
    public int getCurrentStock(Long ingredientId) {
        // Implementasi logika menghitung stok berdasarkan StockMovement
        // Ambil semua pergerakan untuk bahan ini
        List<com.terracafe.terracafe_backend.model.StockMovement> movements = stockMovementRepository.findByIngredientId(ingredientId);
        int totalIn = 0;
        int totalOut = 0;
        for (com.terracafe.terracafe_backend.model.StockMovement movement : movements) {
            if (movement.getMovementType() == com.terracafe.terracafe_backend.model.StockMovement.MovementType.IN) {
                totalIn += movement.getQuantity();
            } else if (movement.getMovementType() == com.terracafe.terracafe_backend.model.StockMovement.MovementType.OUT) {
                totalOut += movement.getQuantity();
            }
        }
        return totalIn - totalOut;
    }

    // Metode untuk mengecek apakah stok rendah
    public boolean isLowStock(Long ingredientId) {
        Ingredient ingredient = getIngredientById(ingredientId)
            .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientId)); // Or throw custom exception
        int currentStock = getCurrentStock(ingredientId);
        return currentStock < ingredient.getMinimumStockThreshold();
    }
}
