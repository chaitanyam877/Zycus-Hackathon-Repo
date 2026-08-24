package com.chaitanya.backend.event;

public record InventoryChangedEvent(
        String productId
) {
}
