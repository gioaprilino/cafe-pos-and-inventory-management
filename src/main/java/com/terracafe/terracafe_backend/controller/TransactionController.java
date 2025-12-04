package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.dto.SalesReportProjection; // Import DTO
import com.terracafe.terracafe_backend.dto.TransactionItemRequest;
import com.terracafe.terracafe_backend.model.Transaction;
import com.terracafe.terracafe_backend.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions") // Base path untuk endpoint transaction
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('CASHIER')")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('CASHIER')")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint untuk membuat transaksi baru (untuk kasir)
    @PostMapping
    @PreAuthorize("hasRole('CASHIER')")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody List<TransactionItemRequest> itemRequests, @RequestParam Long cashierId) {
        try {
            Transaction newTransaction = transactionService.createTransaction(itemRequests, cashierId);
            return ResponseEntity.ok(newTransaction);
        } catch (RuntimeException e) {
            // Handle potential errors (e.g., product not found, cashier not found, insufficient stock)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    // Endpoint untuk mengupdate status transaksi (misalnya oleh kasir atau manager)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('CASHIER')")
    public ResponseEntity<Transaction> updateTransactionStatus(@PathVariable Long id, @RequestParam Transaction.TransactionStatus newStatus) {
        try {
            Transaction updatedTransaction = transactionService.updateTransactionStatus(id, newStatus);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            // Handle potential errors (e.g., transaction not found, invalid status transition)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    // Endpoint untuk laporan penjualan (untuk manager)
    @GetMapping("/reports/sales")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<SalesReportProjection>> getDailySalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<SalesReportProjection> report = transactionService.getDailySalesReport(start, end);
        return ResponseEntity.ok(report);
    }

    // DELETE biasanya tidak digunakan untuk transaksi karena penting untuk audit trail
    // Jika tetap dibutuhkan (misalnya hanya untuk draft), tambahkan method delete dengan hati-hati
    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteTransaction(@PathVariable Long id) { ... }
}
