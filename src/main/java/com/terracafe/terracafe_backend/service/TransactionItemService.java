package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.model.Transaction;
import com.terracafe.terracafe_backend.model.TransactionItem;
import com.terracafe.terracafe_backend.repository.TransactionItemRepository; // Pastikan repository ini ada
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionItemService {

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    public List<TransactionItem> getAllTransactionItems() {
        return transactionItemRepository.findAll();
    }

    public Optional<TransactionItem> getTransactionItemById(Long id) {
        return transactionItemRepository.findById(id);
    }

    public List<TransactionItem> getTransactionItemsByTransactionId(Long transactionId) {
        return transactionItemRepository.findByTransactionId(transactionId);
    }

    public List<TransactionItem> getTransactionItemsByProductId(Long productId) {
        return transactionItemRepository.findByProductId(productId);
    }

    // Biasanya item transaksi dibuat/dihapus sebagai bagian dari transaksi keseluruhan
    // Kita bisa membuat method untuk update item (misalnya kuantitas), tapi ini jarang digunakan setelah transaksi selesai
    public TransactionItem updateTransactionItem(Long id, TransactionItem transactionItemDetails) {
         Optional<TransactionItem> existingItemOpt = transactionItemRepository.findById(id);
         if (existingItemOpt.isPresent()) {
             TransactionItem existingItem = existingItemOpt.get();
             // Add validation for transactionItemDetails fields
             validateTransactionItemFields(transactionItemDetails);
             // Validasi: hanya bisa diupdate jika transaksi belum selesai/completed
             if (existingItem.getTransaction().getStatus() == Transaction.TransactionStatus.COMPLETED) {
                 throw new RuntimeException("Cannot update item of a completed transaction.");
             }

             existingItem.setQuantity(transactionItemDetails.getQuantity());
             // Harga dan subtotal mungkin perlu dihitung ulang
             // existingItem.setUnitPrice(...);
             // existingItem.setSubtotal(...);
             return transactionItemRepository.save(existingItem);
         } else {
             throw new RuntimeException("TransactionItem not found with id: " + id); // Or throw custom exception
         }
    }

    public void deleteTransactionItem(Long id) {
        // Check if deletion is allowed (e.g., transaction not completed)
        Optional<TransactionItem> itemOpt = transactionItemRepository.findById(id);
        if (itemOpt.isPresent()) {
            TransactionItem item = itemOpt.get();
            if (item.getTransaction().getStatus() == Transaction.TransactionStatus.COMPLETED) {
                throw new RuntimeException("Cannot delete item from a completed transaction.");
            }
            transactionItemRepository.deleteById(id);
        } else {
            throw new RuntimeException("TransactionItem not found with id: " + id);
        }
    }

    // Metode bantuan untuk validasi field
    private void validateTransactionItemFields(TransactionItem item) {
        if (item.getQuantity() == null || item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
