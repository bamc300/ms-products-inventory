package com.linktic.msproducts.infrastructure.adapter.out.persistence.repository;

import com.linktic.msproducts.infrastructure.adapter.out.persistence.entity.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, UUID> {
}
