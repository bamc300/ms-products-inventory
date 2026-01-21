package com.linktic.msinventory.infrastructure.adapter.out.persistence.repository;

import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.PurchaseEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPurchaseRepository extends JpaRepository<PurchaseEntity, UUID> {
}
