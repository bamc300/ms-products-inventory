package com.linktic.msinventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDto {
  private InventoryData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class InventoryData {
    private String type;
    private String id; // productId
    private InventoryAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class InventoryAttributes {
    private Integer cantidad;
  }
}
