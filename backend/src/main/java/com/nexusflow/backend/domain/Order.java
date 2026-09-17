package com.nexusflow.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // STRING en vez de ORDINAL: si mañana añades un estado en medio del
    // enum, con ORDINAL se corromperían los datos ya guardados (Hibernate
    // guardaria numeros de posicion, no el nombre). Con STRING, la BD
    // guarda literalmente "PENDING", "CONFIRMED", etc. Mas legible y seguro.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // mappedBy = "order": le dice a Hibernate que la relacion ya esta
    // definida desde el otro lado (OrderItem.order), esta es solo la
    // vista inversa, no crea una columna nueva.
    // cascade ALL + orphanRemoval: los items viven y mueren con su pedido.
    // Si sacas un item de esta lista, se borra de la BD automaticamente.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Mantiene consistente la relacion bidireccional: al añadir un item,
     * tambien le decimos a ese item quien es su pedido. Sin esto, es facil
     * olvidar setear un lado de la relacion y que Hibernate guarde datos
     * incoherentes.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
