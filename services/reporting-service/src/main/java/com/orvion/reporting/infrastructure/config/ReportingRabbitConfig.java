package com.orvion.reporting.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportingRabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(ReportingRabbitConfig.class);
    public static final String EXCHANGE = "orvion.reporting.exchange";
    public static final String FINANCE_FACTS_QUEUE = "orvion.reporting.finance.facts";
    public static final String INVENTORY_FACTS_QUEUE = "orvion.reporting.inventory.facts";
    public static final String SALES_FACTS_QUEUE = "orvion.reporting.sales.facts";
    public static final String HCM_FACTS_QUEUE = "orvion.reporting.hcm.facts";
    public static final String MFG_FACTS_QUEUE = "orvion.reporting.manufacturing.facts";
    public static final String AUDIT_LOG_QUEUE = "orvion.audit.log";

    @Bean
    public TopicExchange reportingExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue financeFactsQueue() {
        return QueueBuilder.durable(FINANCE_FACTS_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", FINANCE_FACTS_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue inventoryFactsQueue() {
        return QueueBuilder.durable(INVENTORY_FACTS_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", INVENTORY_FACTS_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue salesFactsQueue() {
        return QueueBuilder.durable(SALES_FACTS_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", SALES_FACTS_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue hcmFactsQueue() {
        return QueueBuilder.durable(HCM_FACTS_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", HCM_FACTS_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue manufacturingFactsQueue() {
        return QueueBuilder.durable(MFG_FACTS_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", MFG_FACTS_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue auditLogQueue() {
        return QueueBuilder.durable(AUDIT_LOG_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", AUDIT_LOG_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Binding financeFactsBinding(Queue financeFactsQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(financeFactsQueue).to(reportingExchange).with("orvion.finance.*");
    }

    @Bean
    public Binding inventoryFactsBinding(Queue inventoryFactsQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(inventoryFactsQueue).to(reportingExchange).with("orvion.inventory.*");
    }

    @Bean
    public Binding salesFactsBinding(Queue salesFactsQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(salesFactsQueue).to(reportingExchange).with("orvion.sales.*");
    }

    @Bean
    public Binding hcmFactsBinding(Queue hcmFactsQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(hcmFactsQueue).to(reportingExchange).with("orvion.hcm.*");
    }

    @Bean
    public Binding manufacturingFactsBinding(Queue manufacturingFactsQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(manufacturingFactsQueue).to(reportingExchange).with("orvion.manufacturing.*");
    }

    @Bean
    public Binding auditLogBinding(Queue auditLogQueue, TopicExchange reportingExchange) {
        return BindingBuilder.bind(auditLogQueue).to(reportingExchange).with("orvion.audit.log");
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
