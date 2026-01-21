package com.linktic.msproducts.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de creación de producto")
public class CreateProductRequestDto {
  @Valid
  @NotNull
  private ProductData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(name = "ProductDataRequest")
  public static class ProductData {
    @Schema(description = "Tipo de recurso", example = "products", hidden = true)
    private String type;

    @Valid
    @NotNull
    private ProductAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductAttributes {
    @NotBlank(message = "Nombre es requerido")
    private String nombre;

    @NotNull(message = "Precio es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "Precio debe ser mayor a 0")
    private BigDecimal precio;

    private String descripcion;
  }
}
