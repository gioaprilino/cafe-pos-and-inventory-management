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
        // TODO: Add validation (e.g., name and unit not empty, minimumStockThreshold >= 0)
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
            // TODO: Add validation for ingredientDetails fields
            existingIngredient.setName(ingredientDetails.getName());
            existingIngredient.setUnit(ingredientDetails.getUnit());
            existingIngredient.setMinimumStockThreshold(ingredientDetails.getMinimumStockThreshold());
            // createdAt tidak diupdate
            return ingredientRepository.save(existingIngredient);
        } else {
            throw new RuntimeException("Ingredient not found with id: " + id); // Or throw custom exception
        }
    }

    public void deleteIngredient(Long id) {
        // TODO: Check if ingredient is associated with any recipes before deleting
        ingredientRepository.deleteById(id);
    }

    // Metode untuk menghitung stok saat ini berdasarkan pergerakan
    public int getCurrentStock(Long ingredientId) {
        // TODO: Implementasi logika menghitung stok berdasarkan StockMovement
        // Contoh sederhana (mungkin perlu query kompleks atau view di database untuk efisiensi):
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
