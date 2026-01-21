package com.linktic.msinventory.infrastructure.adapter.out.persistence.repository;

import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.domain.port.out.InventoryRepositoryPort;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.mapper.InventoryPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

  private final SpringDataInventoryRepository repository;
  private final InventoryPersistenceMapper mapper;

  @Override
  public Optional<Inventory> findByProductId(UUID productId) {
    return repository.findById(productId).map(mapper::toDomain);
  }

  @Override
  public Inventory save(Inventory inventory) {
    InventoryEntity entity = mapper.toEntity(inventory);
    InventoryEntity saved = repository.save(entity);
    return mapper.toDomain(saved);
  }
}
