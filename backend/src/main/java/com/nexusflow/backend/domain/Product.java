package com.nexusflow.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SKU = Stock Keeping Unit: el codigo unico de negocio del producto,
    // distinto del id tecnico autogenerado. Es lo que usaria un operario
    // de almacen, no el id interno de la base de datos.
    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    // BigDecimal, nunca double/float, para dinero: evita errores de
    // redondeo en aritmetica de coma flotante binaria.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

}
