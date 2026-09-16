package com.nexusflow.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controlador minimo para verificar que la aplicacion arranca y responde.
 *
 * No forma parte del dominio de negocio (Pedidos, Inventario...): es
 * infraestructura pura, util para probar que Spring Boot + la BD + Flyway
 * estan correctamente conectados antes de empezar a modelar el dominio real.
 *
 * @RestController = @Controller + @ResponseBody: cada metodo devuelve
 * directamente el cuerpo de la respuesta (en este caso, serializado a JSON
 * automaticamente por Jackson), en vez de resolver una vista HTML.
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "nexusflow-backend",
                "timestamp", Instant.now().toString()
        );
    }

}
