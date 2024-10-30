package project.transaction.management.system.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id;

    @Column(nullable = false, unique = true) // Account number cannot be null and must be unique
    private String accountNumber;

    @Column(nullable = false) // Account type cannot be null
    private String accountType;

    @Column(nullable = false) // Balance cannot be null
    private Double balance;

    @Column(name = "created_at", updatable = false) // Column mapping for createdAt
    private LocalDateTime createdAt;

    @Column(nullable = false) // Account type cannot be null
    private String name;

    @Column(name = "updated_at") // Column mapping for updatedAt
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) // Many accounts to one user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in account table
    private UserEntity user;

    // Automatically set createdAt before insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Set the current time when the record is created
    }

    // Automatically update updatedAt before update
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Set the current time when the record is updated
    }
}