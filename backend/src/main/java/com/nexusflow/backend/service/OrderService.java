package com.nexusflow.backend.service;

import com.nexusflow.backend.domain.Customer;
import com.nexusflow.backend.domain.Order;
import com.nexusflow.backend.domain.OrderItem;
import com.nexusflow.backend.domain.OrderStatus;
import com.nexusflow.backend.domain.Product;
import com.nexusflow.backend.dto.CreateOrderRequest;
import com.nexusflow.backend.dto.OrderItemRequest;
import com.nexusflow.backend.dto.OrderItemResponse;
import com.nexusflow.backend.dto.OrderResponse;
import com.nexusflow.backend.exception.InsufficientStockException;
import com.nexusflow.backend.exception.ResourceNotFoundException;
import com.nexusflow.backend.repository.CustomerRepository;
import com.nexusflow.backend.repository.OrderRepository;
import com.nexusflow.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @RequiredArgsConstructor (Lombok) genera un constructor con todos los
 * campos 'final' como parametros. Spring lo usa para inyectar las
 * dependencias (inyeccion por constructor, la forma recomendada: hace
 * explicito que este servicio SIEMPRE necesita estos 3 repositorios para
 * existir, y permite testearlo facilmente pasando mocks al constructor).
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    /**
     * Crea un pedido completo: valida cliente y stock, congela precios,
     * descuenta stock y calcula el total, todo dentro de una unica
     * transaccion. Si cualquier paso falla (ej. stock insuficiente en la
     * segunda linea), Spring deshace TODO lo anterior automaticamente:
     * no queda ni pedido guardado ni stock descontado a medias.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found: id=" + request.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: id=" + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product '" + product.getSku()
                                + "': requested=" + itemRequest.getQuantity()
                                + ", available=" + product.getStock());
            }

            // "Congelamos" el precio actual del producto en la linea del pedido.
            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            order.addItem(item);

            // No hace falta llamar a productRepository.save(product) de forma
            // explicita: 'product' es una entidad "managed" (fue cargada en
            // esta misma transaccion via findById). Hibernate detecta el
            // cambio de campo (dirty checking) y genera el UPDATE solo al
            // hacer commit de la transaccion.
            product.setStock(product.getStock() - itemRequest.getQuantity());
        }

        order.recalculateTotal();
        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: id=" + id));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

}
