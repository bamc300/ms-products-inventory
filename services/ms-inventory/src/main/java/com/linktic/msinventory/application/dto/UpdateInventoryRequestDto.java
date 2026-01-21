package com.linktic.msinventory.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateInventoryRequestDto {
  @Valid
  @NotNull
  private InventoryUpdateData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class InventoryUpdateData {
    private String type;

    @Valid
    @NotNull
    private InventoryUpdateAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class InventoryUpdateAttributes {
    @NotNull
    private Integer cantidad; // Can be negative to reduce stock manually if needed, or just
                              // positive to add.
  }
}
