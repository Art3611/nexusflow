package com.nexusflow.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada: representa "quiero N unidades del producto P" dentro
 * del cuerpo de la peticion POST /api/orders. No tiene relacion directa
 * con la entidad OrderItem: es solo la forma en que el cliente HTTP
 * expresa su intencion.
 */
@Getter
@Setter
public class OrderItemRequest {

    @NotNull(message = "productId es obligatorio")
    private Long productId;

    @NotNull(message = "quantity es obligatorio")
    @Min(value = 1, message = "quantity debe ser al menos 1")
    private Integer quantity;

}
