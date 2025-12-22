package com.terracafe.terracafe_backend.service;

import com.terracafe.terracafe_backend.dto.SalesReportProjection; // Import DTO yang telah dibuat
import com.terracafe.terracafe_backend.dto.TransactionItemRequest;
import com.terracafe.terracafe_backend.model.*;
import com.terracafe.terracafe_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Untuk generate nomor transaksi unik

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private ProductRepository productRepository; // Untuk validasi dan ambil harga

    @Autowired
    private RecipeService recipeService; // Untuk mengurangi stok

    @Autowired
    private StockMovementService stockMovementService; // Untuk mencatat pengurangan stok

    @Autowired
    private UserService userService; // Untuk validasi kasir

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Contoh method untuk membuat transaksi baru (ini adalah inti dari logika
    // bisnis)
    public Transaction createTransaction(List<TransactionItemRequest> itemRequests, Long cashierId) { // TransactionItemRequest
                                                                                                      // adalah DTO
                                                                                                      // untuk input
        // 1. Validasi kasir
        User cashier = userService.getUserById(cashierId)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + cashierId)); // Or throw custom
                                                                                                     // exception

        // 2. Hitung total dan validasi item
        BigDecimal totalAmount = BigDecimal.ZERO;
        Transaction transaction = new Transaction();
        transaction.setCashier(cashier);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED); // Set langsung ke COMPLETED untuk POS kasir
        transaction.setTransactionNumber(generateTransactionNumber()); // Generate nomor unik

        for (TransactionItemRequest requestItem : itemRequests) {
            // Ambil produk dari database
            Product product = productRepository.findById(requestItem.getProductId())
                    .orElseThrow(
                            () -> new RuntimeException("Product not found with id: " + requestItem.getProductId())); // Or
                                                                                                                     // throw
                                                                                                                     // custom
                                                                                                                     // exception

            // Validasi stok produk aktif (opsional)
            if (!product.getIsActive()) {
                throw new RuntimeException("Cannot add inactive product '" + product.getName() + "' to transaction.");
            }

            // Hitung subtotal untuk item ini
            BigDecimal unitPrice = product.getPrice(); // Harga saat transaksi
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(requestItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            // Buat TransactionItem
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setTransaction(transaction); // Belum disimpan ke DB, ID-nya belum ada
            transactionItem.setProduct(product);
            transactionItem.setQuantity(requestItem.getQuantity());
            transactionItem.setUnitPrice(unitPrice);
            transactionItem.setSubtotal(subtotal);

            transaction.getItems().add(transactionItem); // Tambahkan ke list transaksi
        }

        transaction.setTotalAmount(totalAmount);

        // 3. Simpan transaksi (ini akan menyimpan juga item-item karena cascade)
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 4. (Opsional) Jika status langsung COMPLETED, kurangi stok otomatis
        if (savedTransaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
            consumeStockForTransaction(savedTransaction);
        }

        return savedTransaction;
    }

    // Metode untuk mengurangi stok berdasarkan resep produk yang terjual
    private void consumeStockForTransaction(Transaction transaction) {
        // Implementasikan logika untuk mengurangi stok
        // Ambil item transaksi
        for (TransactionItem item : transaction.getItems()) {
            Long productId = item.getProduct().getId();
            int quantitySold = item.getQuantity();

            // Ambil resep produk
            List<Recipe> recipes = recipeService.getRecipesByProductId(productId);

            // Loop resep, hitung kebutuhan total bahan, kurangi stok
            for (Recipe recipe : recipes) {
                Long ingredientId = recipe.getIngredient().getId();
                BigDecimal quantityNeededPerUnit = recipe.getQuantityNeeded();
                // Hitung total yang harus dikurangi
                BigDecimal totalQuantityToConsume = quantityNeededPerUnit.multiply(BigDecimal.valueOf(quantitySold));

                // Buat StockMovement untuk pengurangan
                StockMovement stockOutMovement = new StockMovement();
                Ingredient ingredient = new Ingredient(); // Ambil dari repository berdasarkan ID
                ingredient.setId(ingredientId);
                stockOutMovement.setIngredient(ingredient);
                stockOutMovement.setMovementType(StockMovement.MovementType.OUT);
                stockOutMovement.setQuantity(totalQuantityToConsume.intValue()); // Konversi BigDecimal ke int
                stockOutMovement.setReferenceType("TRANSACTION");
                stockOutMovement.setReferenceId(transaction.getId());
                stockOutMovement.setDescription("Consumed for " + quantitySold + " x " + item.getProduct().getName());
                stockOutMovement.setCreatedByUser(transaction.getCashier());

                // Simpan pergerakan stok (ini akan memicu perhitungan ulang stok)
                stockMovementService.saveStockMovement(stockOutMovement);
            }
        }
    }

    // Metode untuk mengupdate status transaksi (misalnya dari PENDING ke COMPLETED)
    public Transaction updateTransactionStatus(Long transactionId, Transaction.TransactionStatus newStatus) {
        Optional<Transaction> existingTransactionOpt = transactionRepository.findById(transactionId);
        if (existingTransactionOpt.isPresent()) {
            Transaction existingTransaction = existingTransactionOpt.get();
            // Validasi status baru (misalnya tidak bisa dari CANCELLED ke COMPLETED)
            // ... logika validasi status ...

            existingTransaction.setStatus(newStatus);
            if (newStatus == Transaction.TransactionStatus.COMPLETED && existingTransaction.getCompletedAt() == null) {
                existingTransaction.setCompletedAt(LocalDateTime.now());
                // Kurangi stok jika status menjadi COMPLETED dan sebelumnya belum dikurangi
                consumeStockForTransaction(existingTransaction);
            }

            return transactionRepository.save(existingTransaction);
        } else {
            throw new RuntimeException("Transaction not found with id: " + transactionId); // Or throw custom exception
        }
    }

    public void deleteTransaction(Long id) {
        // Check if deletion is allowed based on status
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();
            if (transaction.getStatus() == Transaction.TransactionStatus.COMPLETED) {
                throw new RuntimeException("Cannot delete a completed transaction. Status: " + transaction.getStatus());
            }
            transactionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
    }

    // Contoh method untuk laporan penjualan (menggunakan DTO/Projection)
    public List<SalesReportProjection> getDailySalesReport(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findDailySalesReport(start, end);
    }

    // Metode bantuan untuk generate nomor transaksi
    private String generateTransactionNumber() {
        // Format: TC-YYYYMMDD-UUID
        String datePart = LocalDateTime.now().toString().substring(0, 10).replace("-", ""); // Ambil YYYYMMDD
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase(); // Ambil 8 karakter UUID
        return "TC-" + datePart + "-" + uniquePart;
    }
}
