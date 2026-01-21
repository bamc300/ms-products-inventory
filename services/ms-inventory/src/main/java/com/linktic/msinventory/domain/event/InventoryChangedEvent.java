package com.linktic.msinventory.domain.event;

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
public class InventoryChangedEvent {
    private UUID productId;
    private Integer newQuantity;
    private LocalDateTime timestamp;
    private String reason;
}
