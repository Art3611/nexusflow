package com.nexusflow.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // La unicidad tambien se refuerza en la migracion SQL (UNIQUE constraint).
    // Aqui la anotacion sirve para que Hibernate la valide contra el esquema.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId;

    // Hibernate rellena esta columna automaticamente al insertar,
    // sin que nosotros tengamos que hacerlo manualmente en cada servicio.
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
