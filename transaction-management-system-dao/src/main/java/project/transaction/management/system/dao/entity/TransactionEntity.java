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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id", nullable = true)
    private AccountEntity targetAccount;

    @Column(name = "transaction_type", nullable = false)
    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @Column(name = "amount", nullable = false)
    @NotNull(message = "Amount is required")
    private Double amount;

    @Column(name = "timestamp", nullable = false)
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;

    @Column(name = "description")
    private String description;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
