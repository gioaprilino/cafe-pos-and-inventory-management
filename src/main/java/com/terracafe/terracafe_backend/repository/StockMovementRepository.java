package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    // Temukan pergerakan stok berdasarkan ID bahan
    List<StockMovement> findByIngredientId(Long ingredientId);

    // Temukan pergerakan stok berdasarkan ID bahan dan tipe gerakan
    List<StockMovement> findByIngredientIdAndMovementType(Long ingredientId, StockMovement.MovementType movementType);

    // Temukan pergerakan stok berdasarkan reference_type dan reference_id (misalnya, semua gerakan terkait transaksi X)
    List<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);

    // Contoh method tambahan untuk laporan (misalnya, gerakan dalam rentang tanggal)
    List<StockMovement> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
