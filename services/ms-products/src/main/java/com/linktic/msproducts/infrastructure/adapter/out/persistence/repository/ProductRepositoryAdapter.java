package com.linktic.msproducts.infrastructure.adapter.out.persistence.repository;

import com.linktic.msproducts.domain.model.Product;
import com.linktic.msproducts.domain.port.out.ProductRepositoryPort;
import com.linktic.msproducts.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.linktic.msproducts.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

  private final SpringDataProductRepository repository;
  private final ProductPersistenceMapper mapper;

  @Override
  public Product save(Product product) {
    ProductEntity entity = mapper.toEntity(product);
    ProductEntity saved = repository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Product> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }
}
