package com.linktic.msinventory.application.usecase;

import com.linktic.msinventory.domain.event.InventoryChangedEvent;
import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.domain.port.out.EventPublisherPort;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.domain.port.out.ProductClientPort;
import com.linktic.msinventory.domain.port.out.PurchaseRepositoryPort;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseUseCaseTest {

  @Mock
  private InventoryRepositoryPort inventoryRepository;

  @Mock
  private ProductClientPort productClient;

  @Mock
  private PurchaseRepositoryPort purchaseRepository;

  @Mock
  private EventPublisherPort eventPublisher;

  @InjectMocks
  private PurchaseUseCaseImpl purchaseUseCase;

  @Test
  void purchase_ShouldSucceed_WhenStockIsSufficient() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Integer quantity = 2;

    when(productClient.getProductById(productId))
        .thenReturn(Optional.of(ProductClientPort.ProductDto.builder().id(productId)
            .nombre("Test Product").precio(new BigDecimal("100")).build()));

    when(inventoryRepository.findByProductId(productId))
        .thenReturn(Optional.of(Inventory.builder().productId(productId).cantidad(10).build()));

    when(purchaseRepository.save(any(Purchase.class))).thenAnswer(i -> i.getArgument(0));

    Purchase result = purchaseUseCase.purchase(productId, quantity);

    assertNotNull(result);
    assertEquals(productId, result.getProductId());
    assertEquals(quantity, result.getCantidadComprada());
    verify(inventoryRepository).save(any(Inventory.class));
    verify(purchaseRepository).save(any(Purchase.class));
    verify(eventPublisher).publish(any(InventoryChangedEvent.class));
  }

  @Test
  void purchase_ShouldThrow_WhenStockIsInsufficient() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Integer quantity = 20;

    when(productClient.getProductById(productId))
        .thenReturn(Optional.of(ProductClientPort.ProductDto.builder().id(productId).build()));

    when(inventoryRepository.findByProductId(productId))
        .thenReturn(Optional.of(Inventory.builder().productId(productId).cantidad(10).build()));

    assertThrows(InsufficientStockException.class,
        () -> purchaseUseCase.purchase(productId, quantity));

    verify(inventoryRepository, never()).save(any());
  }

  @Test
  void purchase_ShouldThrow_WhenProductNotFound() {
    UUID productId = UUID.fromString("99999999-9999-9999-9999-999999999999");
    Integer quantity = 1;

    when(productClient.getProductById(productId)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> purchaseUseCase.purchase(productId, quantity));

    verify(inventoryRepository, never()).save(any());
  }

  @Test
  void purchase_ShouldThrow_WhenQuantityIsInvalid() {
    UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    assertThrows(IllegalArgumentException.class, () -> purchaseUseCase.purchase(productId, 0));
    verifyNoInteractions(productClient);
    verifyNoInteractions(inventoryRepository);
  }
}
