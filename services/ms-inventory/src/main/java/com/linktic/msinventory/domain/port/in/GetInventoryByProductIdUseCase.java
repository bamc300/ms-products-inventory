package com.linktic.msinventory.domain.port.in;

import com.linktic.msinventory.domain.model.Inventory;
import java.util.UUID;

public interface GetInventoryByProductIdUseCase {
    Inventory getInventoryByProductId(UUID productId);
}
