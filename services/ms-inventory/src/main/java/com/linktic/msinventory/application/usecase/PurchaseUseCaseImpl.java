package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.domain.port.in.PurchaseUseCase;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
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

  @Override
  @Transactional
  public Purchase purchase(UUID productId, Integer quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than 0");
    }

    // 1. Validate Product (calls external service)
    productClient.getProductById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    // 2. Get Inventory (or default to 0 if not exists, though strictly 404 might be better if
    // inventory record doesn't exist, but prompt implies validation logic)
    // If inventory record doesn't exist, stock is 0.
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

    // 5. Return Purchase
    return Purchase.builder().purchaseId(UUID.randomUUID()).productId(productId)
        .cantidadComprada(quantity).timestamp(LocalDateTime.now()).build();
  }
}
