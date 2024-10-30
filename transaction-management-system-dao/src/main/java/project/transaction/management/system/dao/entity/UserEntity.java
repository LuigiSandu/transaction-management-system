package project.transaction.management.system.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Table(name ="user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremented ID
    private Long id;

    @Column(nullable = false, unique = true) // Username cannot be null and must be unique
    private String username;

    @Column(nullable = false) // Password cannot be null
    private String password;

    @Column(nullable = false, unique = true) // Email cannot be null and must be unique
    private String email;

    @Column(name = "created_at", updatable = false) // Column mapping for createdAt
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // Column mapping for updatedAt
    private LocalDateTime updatedAt;

    // One user can have many accounts
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AccountEntity> accounts;

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
