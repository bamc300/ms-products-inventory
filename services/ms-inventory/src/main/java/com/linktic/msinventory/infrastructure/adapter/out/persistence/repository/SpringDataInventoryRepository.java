package com.linktic.msinventory.infrastructure.adapter.out.persistence.repository;

import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryRepository extends JpaRepository<InventoryEntity, UUID> {
}
