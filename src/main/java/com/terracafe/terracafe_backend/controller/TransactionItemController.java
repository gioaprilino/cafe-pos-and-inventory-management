package com.terracafe.terracafe_backend.controller;

import com.terracafe.terracafe_backend.model.TransactionItem;
import com.terracafe.terracafe_backend.service.TransactionItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transaction-items") // Base path untuk endpoint transaction item
public class TransactionItemController {

    @Autowired
    private TransactionItemService transactionItemService;

    @GetMapping
    public List<TransactionItem> getAllTransactionItems() {
        return transactionItemService.getAllTransactionItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionItem> getTransactionItemById(@PathVariable Long id) {
        Optional<TransactionItem> transactionItem = transactionItemService.getTransactionItemById(id);
        return transactionItem.map(ResponseEntity::ok)
                              .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transaction/{transactionId}")
    public List<TransactionItem> getTransactionItemsByTransactionId(@PathVariable Long transactionId) {
        return transactionItemService.getTransactionItemsByTransactionId(transactionId);
    }

    @GetMapping("/product/{productId}")
    public List<TransactionItem> getTransactionItemsByProductId(@PathVariable Long productId) {
        return transactionItemService.getTransactionItemsByProductId(productId);
    }

    // Update item (misalnya kuantitas) - biasanya hanya jika transaksi belum selesai
    @PutMapping("/{id}")
    public ResponseEntity<TransactionItem> updateTransactionItem(@PathVariable Long id, @RequestBody TransactionItem transactionItemDetails) {
        // TODO: Add authorization check (Manager or Cashier based on context)
        // TODO: Add validation (@Valid)
        try {
            TransactionItem updatedItem = transactionItemService.updateTransactionItem(id, transactionItemDetails);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            // Handle potential errors (e.g., item not found, transaction completed)
            return ResponseEntity.badRequest().body(null); // Or return error details
        }
    }

    // DELETE biasanya tidak digunakan untuk item transaksi karena bagian dari transaksi keseluruhan
    // Jika tetap dibutuhkan (misalnya hanya jika transaksi belum selesai), tambahkan method delete dengan hati-hati
    // @DeleteMapping("/{id}")
    // public ResponseEntity<String> deleteTransactionItem(@PathVariable Long id) { ... }
}
