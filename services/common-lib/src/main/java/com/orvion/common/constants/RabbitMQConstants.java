package com.orvion.common.constants;

public final class RabbitMQConstants {

    private RabbitMQConstants() {
    }

    public static final String EXCHANGE_DIRECT = "orvion.direct";
    public static final String EXCHANGE_TOPIC = "orvion.topic";
    public static final String EXCHANGE_FANOUT = "orvion.fanout";
    public static final String EXCHANGE_DLQ = "orvion.dlq";

    public static final String QUEUE_NOTIFICATION_EMAIL = "orvion.notification.email";
    public static final String QUEUE_NOTIFICATION_SMS = "orvion.notification.sms";
    public static final String QUEUE_NOTIFICATION_PUSH = "orvion.notification.push";
    public static final String QUEUE_INVENTORY_UPDATE = "orvion.inventory.update";
    public static final String QUEUE_FINANCE_LEDGER = "orvion.finance.ledger";
    public static final String QUEUE_FINANCE_INVOICE = "orvion.finance.invoice";
    public static final String QUEUE_ORDER_CREATED = "orvion.order.created";
    public static final String QUEUE_ORDER_SHIPPED = "orvion.order.shipped";
    public static final String QUEUE_SHIPMENT_CREATED = "orvion.shipment.created";
    public static final String QUEUE_REPORT_GENERATE = "orvion.report.generate";

    public static final String ROUTING_KEY_NOTIFICATION_EMAIL = "notification.email";
    public static final String ROUTING_KEY_NOTIFICATION_SMS = "notification.sms";
    public static final String ROUTING_KEY_NOTIFICATION_PUSH = "notification.push";
    public static final String ROUTING_KEY_INVENTORY_UPDATE = "inventory.update";
    public static final String ROUTING_KEY_FINANCE_LEDGER = "finance.ledger";
    public static final String ROUTING_KEY_FINANCE_INVOICE = "finance.invoice";
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_SHIPPED = "order.shipped";
    public static final String ROUTING_KEY_SHIPMENT_CREATED = "shipment.created";
    public static final String ROUTING_KEY_REPORT_GENERATE = "report.generate";

    public static final String DLQ_SUFFIX = ".dlq";
    public static final String ROUTING_KEY_DLQ_PREFIX = "dlq.";
}
