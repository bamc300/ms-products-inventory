package com.linktic.msproducts.application.usecase;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllProductsUseCaseTest {

  @Mock
  private ProductRepositoryPort productRepositoryPort;

  @InjectMocks
  private GetAllProductsUseCaseImpl getAllProductsUseCase;

  @Test
  void getAllProducts_ShouldReturnListOfProducts() {
    Product p1 = Product.builder().id(java.util.UUID.randomUUID()).nombre("P1").precio(BigDecimal.TEN).build();
    Product p2 = Product.builder().id(java.util.UUID.randomUUID()).nombre("P2").precio(BigDecimal.ONE).build();
    
    when(productRepositoryPort.findAll()).thenReturn(Arrays.asList(p1, p2));

    List<Product> result = getAllProductsUseCase.getAllProducts();

    assertEquals(2, result.size());
    assertEquals("P1", result.get(0).getNombre());
  }
}
