package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.in.GetInventoryByProductIdUseCase;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetInventoryByProductIdUseCaseImpl implements GetInventoryByProductIdUseCase {

  private final InventoryRepositoryPort inventoryRepository;

  @Override
  public Inventory getInventoryByProductId(UUID productId) {
    return inventoryRepository.findByProductId(productId)
        .orElse(Inventory.builder().productId(productId).cantidad(0).build());
  }
}
