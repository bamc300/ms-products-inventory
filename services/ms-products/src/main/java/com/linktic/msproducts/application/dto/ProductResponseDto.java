package com.linktic.msproducts.application.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
  private ProductData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductData {
    private String type;
    private String id;
    private ProductAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ProductAttributes {
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
  }
}
