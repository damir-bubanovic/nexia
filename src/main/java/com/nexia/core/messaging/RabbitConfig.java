package com.nexia.core.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "nexia.events";
    public static final String USER_EVENTS_QUEUE = "nexia.core.user-events";
    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";

    @Bean
    public DirectExchange nexiaExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue userEventsQueue() {
        return new Queue(USER_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding userEventsBinding(DirectExchange nexiaExchange, Queue userEventsQueue) {
        return BindingBuilder
                .bind(userEventsQueue)
                .to(nexiaExchange)
                .with(USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
