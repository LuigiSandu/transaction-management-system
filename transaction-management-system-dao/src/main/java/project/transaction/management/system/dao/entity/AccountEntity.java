package project.transaction.management.system.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionEntity> transactions = new ArrayList<>();

    // Optional: Transactions where this account is the target (for transfers)
    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL)
    private List<TransactionEntity> receivedTransactions;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(); // Set the current time when the record is created
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(); // Set the current time when the record is updated
    }
}