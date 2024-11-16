package project.transaction.management.system.dao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@Table(name = "users")
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

    @Column(name = "token_version") // Column mapping for createdAt
    private int tokenVersion = 1;

    @Column(name = "updated_at") // Column mapping for updatedAt
    private LocalDateTime updatedAt;

    // One user can have many accounts
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AccountEntity> accounts;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

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
