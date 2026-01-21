package com.linktic.msinventory.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponseDto {
  private PurchaseData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PurchaseData {
    private String type;
    private UUID id;
    private PurchaseAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PurchaseAttributes {
    private UUID productId;
    private Integer cantidadComprada;
    private LocalDateTime timestamp;
  }
}
