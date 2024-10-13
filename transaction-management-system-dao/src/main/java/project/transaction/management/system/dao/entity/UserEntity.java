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
}
