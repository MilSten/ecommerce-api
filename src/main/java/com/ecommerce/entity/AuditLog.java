package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String entityType;  // "Product", "Order", etc.

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String action;  // "CREATE", "UPDATE", "DELETE"

    @Column(nullable = false)
    private UUID userId;  // Кто сделал действие

    @Column(columnDefinition = "TEXT")
    private String changes;  // JSON с изменениями

    @Column(nullable = false)
    private LocalDateTime createdAt;
}