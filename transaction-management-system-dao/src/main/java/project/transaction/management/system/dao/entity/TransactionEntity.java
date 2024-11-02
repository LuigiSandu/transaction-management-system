package project.transaction.management.system.dao.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private AccountEntity account;

    @Column(name = "transaction_type", nullable = false) // Ensuring transactionType cannot be null
    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @Column(name = "amount", nullable = false) // Ensuring amount cannot be null
    @NotNull(message = "Amount is required")
    private Long amount;

    @Column(name = "timestamp", nullable = false) // Ensuring timestamp cannot be null
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp; // Use LocalDateTime for precise date and time tracking

    @Column(name = "description") // Description can be optional
    private String description;

}
