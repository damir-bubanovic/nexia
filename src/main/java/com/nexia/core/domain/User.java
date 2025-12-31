package com.nexia.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@SuppressWarnings("unused")
public class User {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    protected User() {
        // Required by JPA
    }

    /**
     * New full constructor including auth fields.
     */
    public User(UUID id,
                String email,
                String fullName,
                Instant createdAt,
                String passwordHash,
                String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Backwards-compatible constructor for existing code that doesn’t set auth fields yet.
     * Uses null passwordHash and default role "USER".
     */
    public User(UUID id,
                String email,
                String fullName,
                Instant createdAt) {
        this(id, email, fullName, createdAt, null, "USER");
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}
