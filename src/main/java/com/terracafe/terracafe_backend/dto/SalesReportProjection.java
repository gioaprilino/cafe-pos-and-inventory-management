package com.terracafe.terracafe_backend.dto;

import java.math.BigDecimal;
import java.sql.Date; // Ganti import ke java.sql.Date
// import java.time.LocalDate; // Komentari atau hapus baris ini

// Class ini berfungsi sebagai DTO/Projection untuk hasil query laporan penjualan harian
public class SalesReportProjection {

    // Kolom yang dipilih dalam query: DATE(t.createdAt), SUM(t.totalAmount), COUNT(t.id)
    private Date date; // Ganti tipe menjadi Date
    private BigDecimal totalRevenue; // Total pendapatan pada tanggal tersebut
    private Long transactionCount; // Jumlah transaksi pada tanggal tersebut

    // Constructor harus sesuai dengan urutan dan tipe data kolom yang dipilih dalam query JPA
    // SELECT DATE(t.createdAt), SUM(t.totalAmount), COUNT(t.id)
    // DATE(t.createdAt) -> java.sql.Date (ini yang sering dikembalikan oleh fungsi DATE)
    // SUM(t.totalAmount) -> BigDecimal (karena totalAmount adalah BigDecimal)
    // COUNT(t.id) -> Long (karena COUNT mengembalikan Long)
    public SalesReportProjection(Date date, BigDecimal totalRevenue, Long transactionCount) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.transactionCount = transactionCount;
    }

    // Getters (dan Setters jika diperlukan, meskipun untuk projection biasanya tidak)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    // Optional: Override toString untuk debugging
    @Override
    public String toString() {
        return "SalesReportProjection{" +
               "date=" + date +
               ", totalRevenue=" + totalRevenue +
               ", transactionCount=" + transactionCount +
               '}';
    }
}