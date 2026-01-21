package com.linktic.msinventory.domain.port.out;

import com.linktic.msinventory.domain.event.InventoryChangedEvent;

public interface EventPublisherPort {
    void publish(InventoryChangedEvent event);
}
