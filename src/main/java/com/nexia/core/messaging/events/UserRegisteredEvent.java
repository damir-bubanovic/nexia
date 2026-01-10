package com.nexia.core.messaging.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a new user is registered.
 */
public record UserRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UUID userId,
        String email
) {
    public static UserRegisteredEvent of(UUID userId, String email) {
        return new UserRegisteredEvent(
                UUID.randomUUID(),
                Instant.now(),
                userId,
                email
        );
    }
}
