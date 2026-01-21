package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.event.InventoryChangedEvent;
import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.domain.port.in.PurchaseUseCase;
import com.linktic.msinventory.domain.port.out.EventPublisherPort;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import com.linktic.msinventory.domain.port.out.PurchaseRepositoryPort;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseUseCaseImpl implements PurchaseUseCase {

  private final InventoryRepositoryPort inventoryRepository;
  private final ProductClientPort productClient;
  private final PurchaseRepositoryPort purchaseRepository;
  private final EventPublisherPort eventPublisher;

  @Override
  @Transactional
  public Purchase purchase(UUID productId, Integer quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than 0");
    }

    // 1. Validate Product (calls external service)
    productClient.getProductById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    // 2. Get Inventory
    Inventory inventory = inventoryRepository.findByProductId(productId)
        .orElse(Inventory.builder().productId(productId).cantidad(0).build());

    // 3. Validate Stock
    if (inventory.getCantidad() < quantity) {
      throw new InsufficientStockException(
          "Insufficient stock. Available: " + inventory.getCantidad());
    }

    // 4. Deduct Stock
    inventory.setCantidad(inventory.getCantidad() - quantity);
    inventoryRepository.save(inventory);

    // 5. Create Purchase
    Purchase purchase = Purchase.builder()
        .purchaseId(UUID.randomUUID())
        .productId(productId)
        .cantidadComprada(quantity)
        .timestamp(LocalDateTime.now())
        .build();

    // 6. Save Purchase History
    Purchase savedPurchase = purchaseRepository.save(purchase);

    // 7. Publish Inventory Changed Event
    eventPublisher.publish(InventoryChangedEvent.builder()
        .productId(productId)
        .newQuantity(inventory.getCantidad())
        .timestamp(LocalDateTime.now())
        .reason("Purchase")
        .build());

    return savedPurchase;
  }
}
