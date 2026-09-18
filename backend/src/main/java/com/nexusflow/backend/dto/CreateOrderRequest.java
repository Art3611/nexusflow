package com.nexusflow.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "customerId es obligatorio")
    private Long customerId;

    @NotEmpty(message = "El pedido debe tener al menos una linea")
    // @Valid en cascada: valida tambien cada OrderItemRequest dentro de la lista,
    // no solo que la lista no este vacia.
    private List<@Valid OrderItemRequest> items;

}
