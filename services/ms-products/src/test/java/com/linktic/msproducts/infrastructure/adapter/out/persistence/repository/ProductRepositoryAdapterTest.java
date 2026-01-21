package com.linktic.msproducts.infrastructure.adapter.out.persistence.repository;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.linktic.msproducts.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

  @Mock
  private SpringDataProductRepository repository;

  @Mock
  private ProductPersistenceMapper mapper;

  @InjectMocks
  private ProductRepositoryAdapter adapter;

  @Test
  void save_ShouldMapAndPersist() {
    Product domain = Product.builder().nombre("P1").precio(new BigDecimal("9.99")).build();
    ProductEntity entity =
        ProductEntity.builder().nombre("P1").precio(new BigDecimal("9.99")).build();
    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    ProductEntity savedEntity =
        ProductEntity.builder().id(id).nombre("P1").precio(new BigDecimal("9.99")).build();
    Product savedDomain =
        Product.builder().id(id).nombre("P1").precio(new BigDecimal("9.99")).build();

    when(mapper.toEntity(domain)).thenReturn(entity);
    when(repository.save(any(ProductEntity.class))).thenReturn(savedEntity);
    when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

    Product result = adapter.save(domain);

    assertEquals(id, result.getId());
    assertEquals("P1", result.getNombre());
  }

  @Test
  void findById_ShouldReturnMappedDomain_WhenExists() {
    UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    ProductEntity entity =
        ProductEntity.builder().id(id).nombre("P1").precio(new BigDecimal("9.99")).build();
    Product domain = Product.builder().id(id).nombre("P1").precio(new BigDecimal("9.99")).build();

    when(repository.findById(id)).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    Optional<Product> result = adapter.findById(id);

    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
  }
}
