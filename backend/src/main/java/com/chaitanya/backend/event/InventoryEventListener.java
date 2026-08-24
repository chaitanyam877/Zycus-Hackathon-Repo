package com.chaitanya.backend.event;

import com.chaitanya.backend.service.AgenticLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final AgenticLoopService agenticLoopService;

    @Async
    @EventListener
    public void handleInventoryChanged(
            InventoryChangedEvent event
    ) {

        log.info(
                "🔥 EVENT RECEIVED FOR PRODUCT: {}",
                event.productId()
        );

        agenticLoopService.processInventoryChange(
                event.productId()
        );
    }
}