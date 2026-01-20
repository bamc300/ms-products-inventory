package com.linktic.msinventory.application.mapper;

import com.linktic.msinventory.application.dto.InventoryResponseDto;
import com.linktic.msinventory.application.dto.PurchaseResponseDto;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import org.springframework.stereotype.Component;

@Component
public class InventoryDtoMapper {

  public InventoryResponseDto toResponse(Inventory inventory) {
    if (inventory == null)
      return null;
    return InventoryResponseDto.builder()
        .data(InventoryResponseDto.InventoryData.builder().type("inventory")
            .id(String.valueOf(inventory.getProductId()))
            .attributes(InventoryResponseDto.InventoryAttributes.builder()
                .cantidad(inventory.getCantidad()).build())
            .build())
        .build();
  }

  public PurchaseResponseDto toResponse(Purchase purchase) {
    if (purchase == null)
      return null;
    return PurchaseResponseDto.builder()
        .data(PurchaseResponseDto.PurchaseData.builder().type("purchases")
            .id(purchase.getPurchaseId())
            .attributes(PurchaseResponseDto.PurchaseAttributes.builder()
                .productId(purchase.getProductId()).cantidadComprada(purchase.getCantidadComprada())
                .timestamp(purchase.getTimestamp()).build())
            .build())
        .build();
  }
}
