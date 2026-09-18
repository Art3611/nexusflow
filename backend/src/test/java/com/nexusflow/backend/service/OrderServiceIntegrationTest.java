package com.nexusflow.backend.service;

import com.nexusflow.backend.domain.Customer;
import com.nexusflow.backend.domain.OrderStatus;
import com.nexusflow.backend.domain.Product;
import com.nexusflow.backend.dto.CreateOrderRequest;
import com.nexusflow.backend.dto.OrderItemRequest;
import com.nexusflow.backend.dto.OrderResponse;
import com.nexusflow.backend.exception.InsufficientStockException;
import com.nexusflow.backend.exception.ResourceNotFoundException;
import com.nexusflow.backend.repository.CustomerRepository;
import com.nexusflow.backend.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test de INTEGRACION (no unitario): usa el contexto real de Spring y la
 * base de datos real de desarrollo (necesita 'docker compose up -d'
 * corriendo). @Transactional en un test hace que, al terminar cada
 * @Test, Spring deshaga (rollback) todo lo insertado - la base de datos
 * queda exactamente igual que antes de ejecutar el test.
 *
 * Mas adelante, cuando veamos Testcontainers, podremos aislar esto del
 * todo con una BD PostgreSQL desechable solo para tests, sin depender de
 * que tengas Docker Compose corriendo manualmente.
 */
@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void createOrder_descuentaStockYCalculaTotalCorrectamente() {
        Customer customer = customerRepository.save(Customer.builder()
                .name("Acme Corp")
                .email("acme@example.com")
                .taxId("TAX-0001")
                .build());

        Product product = productRepository.save(Product.builder()
                .sku("SKU-WIDGET")
                .name("Widget")
                .price(new BigDecimal("10.00"))
                .stock(5)
                .build());

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(customer.getId());
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(2);
        request.setItems(List.of(itemRequest));

        OrderResponse response = orderService.createOrder(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getTotalAmount()).isEqualByComparingTo("20.00");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSubtotal()).isEqualByComparingTo("20.00");

        // Verificamos el efecto colateral (descuento de stock) releyendo
        // el producto directamente de la base de datos.
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(3);
    }

    @Test
    void createOrder_lanzaExcepcionSiNoHayStockSuficiente() {
        Customer customer = customerRepository.save(Customer.builder()
                .name("Acme Corp")
                .email("acme2@example.com")
                .taxId("TAX-0002")
                .build());

        Product product = productRepository.save(Product.builder()
                .sku("SKU-LIMITED")
                .name("Limited Item")
                .price(new BigDecimal("5.00"))
                .stock(1)
                .build());

        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(customer.getId());
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(10); // pedimos mas de lo que hay en stock
        request.setItems(List.of(itemRequest));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class);

        // El stock NO debe haber cambiado: la transaccion se deshizo entera.
        Product unchangedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(unchangedProduct.getStock()).isEqualTo(1);
    }

    @Test
    void createOrder_lanzaExcepcionSiElClienteNoExiste() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerId(999_999L); // id que no existe
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(1);
        request.setItems(List.of(itemRequest));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}
