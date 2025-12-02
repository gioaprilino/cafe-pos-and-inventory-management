package com.terracafe.terracafe_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    @JsonIgnore // Biasanya tidak perlu kembali ke bahan dalam detail gerakan
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING) // Atau EnumType.ORDINAL, tapi STRING lebih aman
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType; // IN, OUT

    @Column(nullable = false)
    private Integer quantity; // Harus positif

    @Column(name = "reference_type")
    private String referenceType; // contoh: 'RESTOCK', 'TRANSACTION', 'WASTE'

    @Column(name = "reference_id")
    private Long referenceId; // ID dari entitas yang menyebabkan pergerakan (misalnya, ID transaksi)

    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser; // User yang mencatat pergerakan

    // Enum untuk movement_type
    public enum MovementType {
        IN, OUT
    }

    // Constructors
    public StockMovement() {}
    public StockMovement(Ingredient ingredient, MovementType movementType, Integer quantity, String referenceType, Long referenceId, String description, User createdByUser) {
        this.ingredient = ingredient;
        this.movementType = movementType;
        this.quantity = quantity;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.description = description;
        this.createdByUser = createdByUser;
    }

    // PrePersist
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }
}
