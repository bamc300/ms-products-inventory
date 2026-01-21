package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInventoryByProductIdUseCaseTest {

  @Mock
  private InventoryRepositoryPort inventoryRepository;

  @InjectMocks
  private GetInventoryByProductIdUseCaseImpl useCase;

  @Test
  void getInventoryByProductId_ShouldReturnExistingInventory() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(inventoryRepository.findByProductId(productId))
        .thenReturn(Optional.of(Inventory.builder().productId(productId).cantidad(9).build()));

    Inventory result = useCase.getInventoryByProductId(productId);

    assertEquals(productId, result.getProductId());
    assertEquals(9, result.getCantidad());
  }

  @Test
  void getInventoryByProductId_ShouldReturnDefault_WhenMissing() {
    UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

    Inventory result = useCase.getInventoryByProductId(productId);

    assertEquals(productId, result.getProductId());
    assertEquals(0, result.getCantidad());
  }
}
