package com.linktic.msinventory.domain.port.in;

import com.linktic.msinventory.domain.model.Inventory;
import java.util.UUID;

public interface UpdateInventoryUseCase {
    Inventory updateStock(UUID productId, Integer quantity);
}
