package com.linktic.msinventory.infrastructure.adapter.out.persistence.mapper;

import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.PurchaseEntity;
import org.springframework.stereotype.Component;

@Component
public class PurchasePersistenceMapper {

    public PurchaseEntity toEntity(Purchase domain) {
        if (domain == null) {
            return null;
        }
        return PurchaseEntity.builder()
                .id(domain.getPurchaseId())
                .productId(domain.getProductId())
                .quantity(domain.getCantidadComprada())
                .timestamp(domain.getTimestamp())
                .build();
    }

    public Purchase toDomain(PurchaseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Purchase.builder()
                .purchaseId(entity.getId())
                .productId(entity.getProductId())
                .cantidadComprada(entity.getQuantity())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
