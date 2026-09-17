package com.nexusflow.backend.repository;

import com.nexusflow.backend.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JpaRepository<Customer, Long> ya nos da, gratis y sin escribir una sola
 * linea de SQL: findAll(), findById(), save(), delete(), etc.
 * Los metodos "custom" como el de abajo se generan solo a partir del
 * nombre del metodo (Spring Data JPA los interpreta y construye la query).
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

}
