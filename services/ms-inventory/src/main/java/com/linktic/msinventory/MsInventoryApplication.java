package com.linktic.msinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Inventario (Inventory Service).
 *
 * Este servicio gestiona el stock y las transacciones de compra. Sus principales responsabilidades
 * son: - Consultar y actualizar el stock disponible de los productos. - Procesar compras, validando
 * disponibilidad. - Comunicarse con el Microservicio de Productos para validar la existencia de los
 * items antes de cualquier operación de compra.
 */
@SpringBootApplication
public class MsInventoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsInventoryApplication.class, args);
  }

}
