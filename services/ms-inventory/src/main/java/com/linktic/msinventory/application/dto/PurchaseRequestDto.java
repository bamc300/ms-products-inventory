package com.linktic.msinventory.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de compra")
public class PurchaseRequestDto {
  @Valid
  @NotNull
  private PurchaseData data;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(name = "PurchaseDataRequest")
  public static class PurchaseData {
    @Schema(description = "Tipo de recurso", example = "purchases", hidden = true)
    private String type;

    @Valid
    @NotNull
    private PurchaseAttributes attributes;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PurchaseAttributes {
    @NotNull
    private UUID productId;

    @NotNull
    @Min(1)
    private Integer quantity;
  }
}
