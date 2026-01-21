package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.exception.ProductNotFoundException;
import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

  @Mock
  private ProductRepositoryPort productRepositoryPort;

  @InjectMocks
  private GetProductByIdUseCaseImpl getProductByIdUseCase;

  @Test
  void getProductById_ShouldReturnProduct_WhenExists() {
    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Product product = Product.builder().id(id).nombre("P1").precio(new BigDecimal("1.00")).build();

    when(productRepositoryPort.findById(id)).thenReturn(Optional.of(product));

    Product result = getProductByIdUseCase.getProductById(id);

    assertEquals(id, result.getId());
    assertEquals("P1", result.getNombre());
  }

  @Test
  void getProductById_ShouldThrow_WhenNotFound() {
    UUID id = UUID.fromString("99999999-9999-9999-9999-999999999999");
    when(productRepositoryPort.findById(id)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> getProductByIdUseCase.getProductById(id));
  }
}
