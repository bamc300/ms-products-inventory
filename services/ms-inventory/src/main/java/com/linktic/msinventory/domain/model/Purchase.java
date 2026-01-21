package com.linktic.msinventory.domain.model;

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
public class Purchase {
  private UUID purchaseId;
  private UUID productId;
  private Integer cantidadComprada;
  private LocalDateTime timestamp;
}
