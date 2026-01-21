package com.linktic.msinventory.infrastructure.adapter.out.event;

import com.linktic.msinventory.domain.event.InventoryChangedEvent;
import com.linktic.msinventory.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringEventPublisherAdapter implements EventPublisherPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(InventoryChangedEvent event) {
        log.info("Publishing InventoryChangedEvent: {}", event);
        applicationEventPublisher.publishEvent(event);
    }
}
