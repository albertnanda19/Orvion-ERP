package com.orvion.manufacturing.infrastructure.config;

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
public class ManufacturingRabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(ManufacturingRabbitConfig.class);
    public static final String EXCHANGE = "orvion.manufacturing.exchange";
    public static final String WORK_ORDER_COMPLETED_QUEUE = "orvion.manufacturing.work.order.completed";
    public static final String QUALITY_INSPECTION_COMPLETED_QUEUE = "orvion.manufacturing.quality.inspection.completed";

    @Bean
    public TopicExchange manufacturingExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue workOrderCompletedQueue() {
        return QueueBuilder.durable(WORK_ORDER_COMPLETED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", WORK_ORDER_COMPLETED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue qualityInspectionCompletedQueue() {
        return QueueBuilder.durable(QUALITY_INSPECTION_COMPLETED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", QUALITY_INSPECTION_COMPLETED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Binding workOrderCompletedBinding(@Qualifier("workOrderCompletedQueue") Queue workOrderCompletedQueue, TopicExchange manufacturingExchange) {
        return BindingBuilder.bind(workOrderCompletedQueue).to(manufacturingExchange).with("orvion.manufacturing.work.order.completed");
    }

    @Bean
    public Binding qualityInspectionCompletedBinding(@Qualifier("qualityInspectionCompletedQueue") Queue qualityInspectionCompletedQueue, TopicExchange manufacturingExchange) {
        return BindingBuilder.bind(qualityInspectionCompletedQueue).to(manufacturingExchange).with("orvion.manufacturing.quality.inspection.completed");
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
