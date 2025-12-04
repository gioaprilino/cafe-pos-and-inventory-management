package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.StockMovement;
import com.terracafe.terracafe_backend.service.StockMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stock-movements") // Base path untuk endpoint stock movement
public class StockMovementController {

    @Autowired
    private StockMovementService stockMovementService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<StockMovement> getAllStockMovements() {
        return stockMovementService.getAllStockMovements();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public ResponseEntity<StockMovement> getStockMovementById(@PathVariable Long id) {
        Optional<StockMovement> stockMovement = stockMovementService.getStockMovementById(id);
        return stockMovement.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<StockMovement> getStockMovementsByIngredientId(@PathVariable Long ingredientId) {
        return stockMovementService.getStockMovementsByIngredientId(ingredientId);
    }

    @GetMapping("/ingredient/{ingredientId}/type/{type}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public List<StockMovement> getStockMovementsByIngredientIdAndType(@PathVariable Long ingredientId, @PathVariable StockMovement.MovementType type) {
        return stockMovementService.getStockMovementsByIngredientIdAndType(ingredientId, type);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('KITCHEN')")
    public ResponseEntity<StockMovement> createStockMovement(@Valid @RequestBody StockMovement stockMovement) {
        try {
            StockMovement savedMovement = stockMovementService.saveStockMovement(stockMovement);
            return ResponseEntity.ok(savedMovement);
        } catch (RuntimeException e) {
            // Handle potential errors (e.g., insufficient stock for OUT, ingredient/user not found)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    // DELETE biasanya tidak digunakan untuk pergerakan stok karena penting untuk audit trail
    // Jika tetap dibutuhkan, tambahkan method delete dengan hati-hati
    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteStockMovement(@PathVariable Long id) { ... }

}