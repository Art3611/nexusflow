package com.nexusflow.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * @SpringBootApplication es una anotacion "combo" que agrupa tres cosas:
 *  - @Configuration: esta clase puede definir beans de Spring.
 *  - @EnableAutoConfiguration: Spring Boot configura automaticamente lo que
 *    detecta en el classpath (ej. si ve el driver de Postgres, configura un
 *    DataSource).
 *  - @ComponentScan: escanea este paquete y subpaquetes buscando
 *    @Component, @Service, @Repository, @RestController, etc.
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
