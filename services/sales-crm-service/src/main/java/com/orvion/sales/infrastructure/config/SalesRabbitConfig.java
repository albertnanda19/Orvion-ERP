package com.orvion.sales.infrastructure.config;

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
public class SalesRabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(SalesRabbitConfig.class);
    public static final String EXCHANGE = "orvion.sales.exchange";
    public static final String ORDER_CONFIRMED_QUEUE = "orvion.sales.order.confirmed";
    public static final String LEAD_CONVERTED_QUEUE = "orvion.sales.lead.converted";

    @Bean
    public TopicExchange salesExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable(ORDER_CONFIRMED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", ORDER_CONFIRMED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue leadConvertedQueue() {
        return QueueBuilder.durable(LEAD_CONVERTED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", LEAD_CONVERTED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Binding orderConfirmedBinding(@Qualifier("orderConfirmedQueue") Queue queue, TopicExchange salesExchange) {
        return BindingBuilder.bind(queue).to(salesExchange).with("orvion.sales.order.confirmed");
    }

    @Bean
    public Binding leadConvertedBinding(@Qualifier("leadConvertedQueue") Queue queue, TopicExchange salesExchange) {
        return BindingBuilder.bind(queue).to(salesExchange).with("orvion.sales.lead.converted");
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
