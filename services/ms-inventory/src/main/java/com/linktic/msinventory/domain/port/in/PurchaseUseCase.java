package com.linktic.msinventory.domain.port.in;

import com.linktic.msinventory.domain.model.Purchase;
import java.util.UUID;

public interface PurchaseUseCase {
    Purchase purchase(UUID productId, Integer quantity);
}
