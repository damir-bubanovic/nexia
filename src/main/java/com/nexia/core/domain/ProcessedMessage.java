package com.nexia.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected ProcessedMessage() {
        // for JPA
    }

    public ProcessedMessage(UUID eventId, Instant processedAt) {
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public static ProcessedMessage now(UUID eventId) {
        return new ProcessedMessage(eventId, Instant.now());
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
