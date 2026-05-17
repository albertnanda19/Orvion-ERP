package com.orvion.hcm.infrastructure.config;

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
public class HcmRabbitConfig {
    private static final Logger log = LoggerFactory.getLogger(HcmRabbitConfig.class);
    public static final String EXCHANGE = "orvion.hcm.exchange";
    public static final String EMPLOYEE_ONBOARDED_QUEUE = "orvion.hcm.employee.onboarded";
    public static final String PAYROLL_PROCESSED_QUEUE = "orvion.hcm.payroll.processed";
    public static final String FINANCE_PAYMENT_PROCESSED_QUEUE = "orvion.finance.payment.processed";

    @Bean
    public TopicExchange hcmExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue employeeOnboardedQueue() {
        return QueueBuilder.durable(EMPLOYEE_ONBOARDED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", EMPLOYEE_ONBOARDED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue payrollProcessedQueue() {
        return QueueBuilder.durable(PAYROLL_PROCESSED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", PAYROLL_PROCESSED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Queue financePaymentProcessedQueue() {
        return QueueBuilder.durable(FINANCE_PAYMENT_PROCESSED_QUEUE)
            .withArgument("x-dead-letter-exchange", EXCHANGE)
            .withArgument("x-dead-letter-routing-key", FINANCE_PAYMENT_PROCESSED_QUEUE + ".dlq")
            .build();
    }

    @Bean
    public Binding employeeOnboardedBinding(@Qualifier("employeeOnboardedQueue") Queue employeeOnboardedQueue, TopicExchange hcmExchange) {
        return BindingBuilder.bind(employeeOnboardedQueue).to(hcmExchange).with("orvion.hcm.employee.onboarded");
    }

    @Bean
    public Binding payrollProcessedBinding(@Qualifier("payrollProcessedQueue") Queue payrollProcessedQueue, TopicExchange hcmExchange) {
        return BindingBuilder.bind(payrollProcessedQueue).to(hcmExchange).with("orvion.hcm.payroll.processed");
    }

    @Bean
    public Binding financePaymentProcessedBinding(@Qualifier("financePaymentProcessedQueue") Queue financePaymentProcessedQueue, TopicExchange hcmExchange) {
        return BindingBuilder.bind(financePaymentProcessedQueue).to(hcmExchange).with("orvion.finance.payment.processed");
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
