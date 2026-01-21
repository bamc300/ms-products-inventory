package com.linktic.msinventory.infrastructure.adapter.out.persistence.repository;

import com.linktic.msinventory.domain.model.Purchase;
import com.linktic.msinventory.domain.port.out.PurchaseRepositoryPort;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.entity.PurchaseEntity;
import com.linktic.msinventory.infrastructure.adapter.out.persistence.mapper.PurchasePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseRepositoryAdapter implements PurchaseRepositoryPort {

    private final SpringDataPurchaseRepository purchaseRepository;
    private final PurchasePersistenceMapper purchaseMapper;

    @Override
    public Purchase save(Purchase purchase) {
        PurchaseEntity entity = purchaseMapper.toEntity(purchase);
        PurchaseEntity savedEntity = purchaseRepository.save(entity);
        return purchaseMapper.toDomain(savedEntity);
    }
}
