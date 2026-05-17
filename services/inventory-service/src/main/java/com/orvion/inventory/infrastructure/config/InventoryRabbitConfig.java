package com.orvion.inventory.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryRabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(InventoryRabbitConfig.class);
    public static final String EXCHANGE = "orvion.inventory.exchange";
    public static final String SALES_ORDER_CONFIRMED_QUEUE = "orvion.inventory.sales.order.confirmed";
    public static final String PO_APPROVED_QUEUE = "orvion.inventory.purchase.order.approved";

    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue salesOrderConfirmedQueue() {
        return QueueBuilder.durable(SALES_ORDER_CONFIRMED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", SALES_ORDER_CONFIRMED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue poApprovedQueue() {
        return QueueBuilder.durable(PO_APPROVED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", PO_APPROVED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Binding salesOrderBinding(@Qualifier("salesOrderConfirmedQueue") Queue salesOrderConfirmedQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(salesOrderConfirmedQueue).to(inventoryExchange).with("orvion.sales.order.confirmed");
    }

    @Bean
    public Binding poApprovedBinding(@Qualifier("poApprovedQueue") Queue poApprovedQueue, TopicExchange inventoryExchange) {
        return BindingBuilder.bind(poApprovedQueue).to(inventoryExchange).with("orvion.purchase.order.approved");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setConfirmCallback((CorrelationData cd, boolean ack, String cause) -> {
            if (!ack) log.warn("Message not confirmed: {}", cause);
        });
        return template;
    }
}
