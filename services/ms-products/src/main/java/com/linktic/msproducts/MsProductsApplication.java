package com.linktic.msproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Productos (Products Service).
 *
 * Este servicio es responsable de la gestión del catálogo de productos. Sus principales funciones
 * incluyen: - Creación de nuevos productos. - Consulta de información de productos por ID.
 *
 * Actúa como la fuente de verdad para los atributos descriptivos del producto (nombre, precio,
 * descripción).
 */
@SpringBootApplication
public class MsProductsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProductsApplication.class, args);
    }

}
