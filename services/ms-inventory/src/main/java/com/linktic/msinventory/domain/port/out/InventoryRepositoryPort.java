package com.linktic.msinventory.domain.port.out;

import com.linktic.msinventory.domain.model.Inventory;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort {
  Optional<Inventory> findByProductId(UUID productId);

  Inventory save(Inventory inventory);
}
