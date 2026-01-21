package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

  @Mock
  private ProductRepositoryPort productRepositoryPort;

  @InjectMocks
  private CreateProductUseCaseImpl createProductUseCase;

  @Test
  void createProduct_ShouldReturnCreatedProduct() {
    Product product =
        Product.builder().nombre("Test Product").precio(new BigDecimal("100.00")).build();

    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Product savedProduct =
        Product.builder().id(id).nombre("Test Product").precio(new BigDecimal("100.00")).build();

    when(productRepositoryPort.save(any(Product.class))).thenReturn(savedProduct);

    Product result = createProductUseCase.createProduct(product);

    assertEquals(id, result.getId());
    assertEquals("Test Product", result.getNombre());
    verify(productRepositoryPort).save(product);
  }
}
