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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateInventoryUseCaseTest {

  @Mock
  private InventoryRepositoryPort inventoryRepository;

  @InjectMocks
  private UpdateInventoryUseCaseImpl useCase;

  @Test
  void updateStock_ShouldCreateInventory_WhenMissing() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

    Inventory result = useCase.updateStock(productId, 5);

    assertEquals(productId, result.getProductId());
    assertEquals(5, result.getCantidad());
  }

  @Test
  void updateStock_ShouldAddQuantity_WhenExists() {
    UUID productId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    when(inventoryRepository.findByProductId(productId))
        .thenReturn(Optional.of(Inventory.builder().productId(productId).cantidad(3).build()));
    when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

    Inventory result = useCase.updateStock(productId, 4);

    assertEquals(7, result.getCantidad());
  }
}
