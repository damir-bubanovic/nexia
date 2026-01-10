package com.nexia.core.messaging;

import com.nexia.core.domain.ProcessedMessage;
import com.nexia.core.messaging.events.UserRegisteredEvent;
import com.nexia.core.repo.ProcessedMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserEventListener.class);

    private final ProcessedMessageRepository processedMessages;

    public UserEventListener(ProcessedMessageRepository processedMessages) {
        this.processedMessages = processedMessages;
    }

    @Transactional
    @RabbitListener(queues = RabbitConfig.USER_EVENTS_QUEUE)
    public void handleUserRegistered(UserRegisteredEvent event) {
        if (processedMessages.existsById(event.eventId())) {
            log.info("Duplicate UserRegisteredEvent ignored: eventId={}", event.eventId());
            return;
        }

        processedMessages.save(ProcessedMessage.now(event.eventId()));

        log.info("Processed UserRegisteredEvent: eventId={}, userId={}, email={}",
                event.eventId(), event.userId(), event.email());
    }
}
