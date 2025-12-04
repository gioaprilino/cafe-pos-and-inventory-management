package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.Ingredient;
import com.terracafe.terracafe_backend.model.StockMovement;
import com.terracafe.terracafe_backend.model.User;
import com.terracafe.terracafe_backend.repository.IngredientRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.StockMovementRepository; // Pastikan repository ini ada
import com.terracafe.terracafe_backend.repository.UserRepository; // Untuk validasi user
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockMovementService {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private IngredientRepository ingredientRepository; // Untuk validasi bahan
    @Autowired
    private UserRepository userRepository; // Untuk validasi user
    
    @Autowired
    private IngredientService ingredientService; // Untuk menghitung stok

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public Optional<StockMovement> getStockMovementById(Long id) {
        return stockMovementRepository.findById(id);
    }

    public List<StockMovement> getStockMovementsByIngredientId(Long ingredientId) {
        return stockMovementRepository.findByIngredientId(ingredientId);
    }

    public List<StockMovement> getStockMovementsByIngredientIdAndType(Long ingredientId, StockMovement.MovementType type) {
        return stockMovementRepository.findByIngredientIdAndMovementType(ingredientId, type);
    }

    public StockMovement saveStockMovement(StockMovement stockMovement) {
        // Add validation (e.g., quantity > 0, movementType valid, ingredient exists, user exists)
        validateIngredientAndUser(stockMovement.getIngredient(), stockMovement.getCreatedByUser());
        validateQuantity(stockMovement.getQuantity());

        // Implement business logic for 'OUT' movements (e.g., check if enough stock)
        if (stockMovement.getMovementType() == StockMovement.MovementType.OUT) {
            int currentStock = ingredientService.getCurrentStock(stockMovement.getIngredient().getId());
            if (currentStock < stockMovement.getQuantity()) {
                throw new RuntimeException("Insufficient stock for ingredient: " + stockMovement.getIngredient().getName() + 
                                         ". Current stock: " + currentStock + ", Required: " + stockMovement.getQuantity());
            }
        }

        return stockMovementRepository.save(stockMovement);
    }

    public void deleteStockMovement(Long id) {
        Optional<StockMovement> movementOpt = stockMovementRepository.findById(id);
        if (movementOpt.isPresent()) {
            stockMovementRepository.deleteById(id);
        } else {
            throw new RuntimeException("StockMovement not found with id: " + id);
        }
    }

    // Metode bantuan untuk validasi bahan dan user
    private void validateIngredientAndUser(Ingredient ingredient, User user) {
        if (ingredient != null && ingredient.getId() != null) {
            if (!ingredientRepository.existsById(ingredient.getId())) {
                throw new RuntimeException("Ingredient not found with id: " + ingredient.getId());
            }
        } else {
            throw new RuntimeException("Ingredient is required for stock movement");
        }
        if (user != null && user.getId() != null) {
            if (!userRepository.existsById(user.getId())) {
                throw new RuntimeException("User not found with id: " + user.getId());
            }
        } else {
            throw new RuntimeException("User is required for stock movement");
        }
    }

    // Metode bantuan untuk validasi kuantitas
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
