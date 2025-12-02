package com.terracafe.terracafe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String unit; // contoh: 'kg', 'liter', 'pcs'

    @Column(name = "minimum_stock_threshold", nullable = false)
    private Integer minimumStockThreshold = 5; // Nilai default

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relasi ke Recipe (satu bahan bisa digunakan di banyak resep)
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Hindari loop jika diakses dari sini
    private List<Recipe> recipes;

    // Relasi ke StockMovement (banyak pergerakan terkait satu bahan)
    @OneToMany(mappedBy = "ingredient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Hindari loop jika diakses dari sini
    private List<StockMovement> stockMovements;

    // Constructors
    public Ingredient() {}
    public Ingredient(String name, String unit, Integer minimumStockThreshold) {
        this.name = name;
        this.unit = unit;
        this.minimumStockThreshold = minimumStockThreshold != null ? minimumStockThreshold : 5;
    }

    // PrePersist dan PreUpdate
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getMinimumStockThreshold() { return minimumStockThreshold; }
    public void setMinimumStockThreshold(Integer minimumStockThreshold) { this.minimumStockThreshold = minimumStockThreshold; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }

    public List<StockMovement> getStockMovements() { return stockMovements; }
    public void setStockMovements(List<StockMovement> stockMovements) { this.stockMovements = stockMovements; }
}