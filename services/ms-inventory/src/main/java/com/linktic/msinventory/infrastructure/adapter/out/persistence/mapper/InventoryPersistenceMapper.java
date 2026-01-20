package com.linktic.msinventory.infrastructure.adapter.out.persistence.mapper;

import com.linktic.msinventory.domain.model.Inventory;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryPersistenceMapper {

  public InventoryEntity toEntity(Inventory domain) {
    if (domain == null)
      return null;
    return InventoryEntity.builder().productId(domain.getProductId()).cantidad(domain.getCantidad())
        .build();
  }

  public Inventory toDomain(InventoryEntity entity) {
    if (entity == null)
      return null;
    return Inventory.builder().productId(entity.getProductId()).cantidad(entity.getCantidad())
        .build();
  }
}
