package com.terracafe.terracafe_backend.repository;

import com.terracafe.terracafe_backend.model.Transaction;
import com.terracafe.terracafe_backend.dto.SalesReportProjection;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Temukan transaksi berdasarkan ID kasir
    List<Transaction> findByCashierId(Long cashierId);

    // Temukan transaksi berdasarkan status
    List<Transaction> findByStatus(Transaction.TransactionStatus status);

    // Temukan transaksi berdasarkan nomor transaksi (unik)
    Optional<Transaction> findByTransactionNumber(String transactionNumber);

    // Contoh method tambahan untuk laporan (misalnya, transaksi dalam rentang tanggal)
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Contoh method untuk laporan penjualan berdasarkan tanggal
    @Query("SELECT new com.terracafe.terracafe_backend.dto.SalesReportProjection(DATE(t.createdAt), SUM(t.totalAmount), COUNT(t.id)) FROM Transaction t WHERE t.status = 'COMPLETED' AND t.createdAt BETWEEN :start AND :end GROUP BY DATE(t.createdAt)")
    List<SalesReportProjection> findDailySalesReport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
