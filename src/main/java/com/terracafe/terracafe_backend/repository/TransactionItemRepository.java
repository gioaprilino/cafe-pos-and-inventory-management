package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    // Temukan item transaksi berdasarkan ID transaksi
    List<TransactionItem> findByTransactionId(Long transactionId);

    // Temukan item transaksi berdasarkan ID produk
    List<TransactionItem> findByProductId(Long productId);

    // Contoh method tambahan jika diperlukan, misalnya jumlah item tertentu terjual dalam rentang waktu
    @Query("SELECT SUM(ti.quantity) FROM TransactionItem ti WHERE ti.product.id = :productId AND ti.transaction.status = 'COMPLETED' AND ti.transaction.createdAt BETWEEN :start AND :end")
    Integer findQuantitySoldByProductIdAndDateRange(@Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
