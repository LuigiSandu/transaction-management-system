package project.transaction.management.system.dao.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Table(name = "transaction")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account; // Source account for the transaction

    @ManyToOne
    @JoinColumn(name = "target_account_id", nullable = true) // Nullable for non-transfer transactions
    private AccountEntity targetAccount; // Target account for transfers

    @Column(name = "transaction_type", nullable = false) // Ensuring transactionType cannot be null
    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @Column(name = "amount", nullable = false) // Ensuring amount cannot be null
    @NotNull(message = "Amount is required")
    private Double amount;

    @Column(name = "timestamp", nullable = false) // Ensuring timestamp cannot be null
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp; // Use LocalDateTime for precise date and time tracking

    @Column(name = "description") // Description can be optional
    private String description;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now(); // Automatically set the timestamp when the transaction is created
    }
}
