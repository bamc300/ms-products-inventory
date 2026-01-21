package com.linktic.msinventory.application.mapper;

import com.linktic.msinventory.application.dto.InventoryResponseDto;
import com.linktic.msinventory.application.dto.PurchaseResponseDto;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InventoryDtoMapperTest {

  private final InventoryDtoMapper mapper = new InventoryDtoMapper();

  @Test
  void toResponse_ShouldMapInventory() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Inventory inventory = Inventory.builder().productId(productId).cantidad(10).build();

    InventoryResponseDto dto = mapper.toResponse(inventory);

    assertEquals("inventory", dto.getData().getType());
    assertEquals(productId.toString(), dto.getData().getId());
    assertEquals(10, dto.getData().getAttributes().getCantidad());
  }

  @Test
  void toResponse_ShouldReturnNull_WhenInventoryIsNull() {
    assertNull(mapper.toResponse((Inventory) null));
  }

  @Test
  void toResponse_ShouldMapPurchase() {
    UUID purchaseId = UUID.randomUUID();
    LocalDateTime timestamp = LocalDateTime.now();
    UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    Purchase purchase = Purchase.builder().purchaseId(purchaseId).productId(productId)
        .cantidadComprada(3).timestamp(timestamp).build();

    PurchaseResponseDto dto = mapper.toResponse(purchase);

    assertEquals("purchases", dto.getData().getType());
    assertEquals(purchaseId, dto.getData().getId());
    assertEquals(productId, dto.getData().getAttributes().getProductId());
    assertEquals(3, dto.getData().getAttributes().getCantidadComprada());
    assertEquals(timestamp, dto.getData().getAttributes().getTimestamp());
  }

  @Test
  void toResponse_ShouldReturnNull_WhenPurchaseIsNull() {
    assertNull(mapper.toResponse((Purchase) null));
  }
}
