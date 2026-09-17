package com.nexusflow.backend.domain;

/**
 * Estados por los que atraviesa un pedido.
 *
 * Esta secuencia de estados no es arbitraria: en la Fase 2, cada transicion
 * entre estados sera el punto donde se publiquen eventos a Kafka
 * (ej. PENDING -> CONFIRMED dispara la verificacion de stock en el futuro
 * inventory-service). Modelarlo bien ahora evita retrabajo despues.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
