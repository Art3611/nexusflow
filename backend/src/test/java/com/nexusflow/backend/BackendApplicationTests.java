package com.nexusflow.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de "smoke test": no comprueba logica de negocio, solo que el
 * ApplicationContext de Spring se levanta sin errores (todos los beans se
 * instancian, la conexion a BD y las migraciones de Flyway se aplican bien).
 * Es el primer test que debe existir en cualquier proyecto Spring Boot.
 */
@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // Si este test falla, algo en la configuracion (BD, beans, propiedades)
        // esta roto. No hace falta assert explicito: el fallo al arrancar
        // el contexto ya hace fallar el test.
    }

}
