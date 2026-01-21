package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.event.InventoryChangedEvent;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.in.UpdateInventoryUseCase;
import com.linktic.msinventory.domain.port.out.EventPublisherPort;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateInventoryUseCaseImpl implements UpdateInventoryUseCase {

  private final InventoryRepositoryPort inventoryRepository;
  private final EventPublisherPort eventPublisher;

  @Override
  @Transactional
  public Inventory updateStock(UUID productId, Integer quantity) {
    Inventory inventory = inventoryRepository.findByProductId(productId)
        .orElse(Inventory.builder().productId(productId).cantidad(0).build());

    inventory.setCantidad(inventory.getCantidad() + quantity);
    Inventory savedInventory = inventoryRepository.save(inventory);

    eventPublisher.publish(InventoryChangedEvent.builder()
        .productId(productId)
        .newQuantity(savedInventory.getCantidad())
        .timestamp(LocalDateTime.now())
        .reason("Stock Update")
        .build());

    return savedInventory;
  }
}
