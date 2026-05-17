package com.orvion.notification.infrastructure.config;

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
public class NotificationRabbitConfig {

    private static final Logger log = LoggerFactory.getLogger(NotificationRabbitConfig.class);
    public static final String EXCHANGE = "orvion.notification.exchange";

    public static final String INVOICE_CREATED_QUEUE = "orvion.notification.finance.invoice.created";
    public static final String PAYMENT_PROCESSED_QUEUE = "orvion.notification.finance.payment.processed";
    public static final String REORDER_TRIGGERED_QUEUE = "orvion.notification.inventory.reorder.triggered";
    public static final String EMPLOYEE_ONBOARDED_QUEUE = "orvion.notification.hcm.employee.onboarded";
    public static final String PAYROLL_PROCESSED_QUEUE = "orvion.notification.hcm.payroll.processed";
    public static final String ORDER_CONFIRMED_QUEUE = "orvion.notification.sales.order.confirmed";
    public static final String WORK_ORDER_COMPLETED_QUEUE = "orvion.notification.manufacturing.work.order.completed";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue invoiceCreatedQueue() {
        return QueueBuilder.durable(INVOICE_CREATED_QUEUE).build();
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESSED_QUEUE).build();
    }

    @Bean
    public Queue reorderTriggeredQueue() {
        return QueueBuilder.durable(REORDER_TRIGGERED_QUEUE).build();
    }

    @Bean
    public Queue employeeOnboardedQueue() {
        return QueueBuilder.durable(EMPLOYEE_ONBOARDED_QUEUE).build();
    }

    @Bean
    public Queue payrollProcessedQueue() {
        return QueueBuilder.durable(PAYROLL_PROCESSED_QUEUE).build();
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return QueueBuilder.durable(ORDER_CONFIRMED_QUEUE).build();
    }

    @Bean
    public Queue workOrderCompletedQueue() {
        return QueueBuilder.durable(WORK_ORDER_COMPLETED_QUEUE).build();
    }

    @Bean
    public Binding invoiceCreatedBinding(@Qualifier("invoiceCreatedQueue") Queue invoiceCreatedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(invoiceCreatedQueue).to(notificationExchange).with("orvion.finance.invoice.created");
    }

    @Bean
    public Binding paymentProcessedBinding(@Qualifier("paymentProcessedQueue") Queue paymentProcessedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(paymentProcessedQueue).to(notificationExchange).with("orvion.finance.payment.processed");
    }

    @Bean
    public Binding reorderTriggeredBinding(@Qualifier("reorderTriggeredQueue") Queue reorderTriggeredQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(reorderTriggeredQueue).to(notificationExchange).with("orvion.inventory.reorder.triggered");
    }

    @Bean
    public Binding employeeOnboardedBinding(@Qualifier("employeeOnboardedQueue") Queue employeeOnboardedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(employeeOnboardedQueue).to(notificationExchange).with("orvion.hcm.employee.onboarded");
    }

    @Bean
    public Binding payrollProcessedBinding(@Qualifier("payrollProcessedQueue") Queue payrollProcessedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(payrollProcessedQueue).to(notificationExchange).with("orvion.hcm.payroll.processed");
    }

    @Bean
    public Binding orderConfirmedBinding(@Qualifier("orderConfirmedQueue") Queue orderConfirmedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(orderConfirmedQueue).to(notificationExchange).with("orvion.sales.order.confirmed");
    }

    @Bean
    public Binding workOrderCompletedBinding(@Qualifier("workOrderCompletedQueue") Queue workOrderCompletedQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(workOrderCompletedQueue).to(notificationExchange).with("orvion.manufacturing.work.order.completed");
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
