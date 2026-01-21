package com.linktic.msinventory.domain.port.out;

import com.linktic.msinventory.domain.model.Purchase;

public interface PurchaseRepositoryPort {
    Purchase save(Purchase purchase);
}
