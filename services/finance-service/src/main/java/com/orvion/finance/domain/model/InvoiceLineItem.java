package com.orvion.finance.domain.model;

import com.orvion.finance.domain.model.vo.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Entity
@Table(name = "invoice_line_items")
@Getter
@Setter
@NoArgsConstructor
public class InvoiceLineItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(length = 500, nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "subtotal", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "currency", length = 3))
    })
    private Money subtotal;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "tax_amount", precision = 19, scale = 4)),
        @AttributeOverride(name = "currencyCode", column = @Column(name = "tax_currency", length = 3))
    })
    private Money taxAmount;

    @Column(name = "line_number")
    private int lineNumber;

    public InvoiceLineItem(String description, BigDecimal quantity, BigDecimal unitPrice,
                           BigDecimal taxRate, String currency) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxRate = taxRate;
        BigDecimal rawSubtotal = quantity.multiply(unitPrice);
        this.subtotal = new Money(rawSubtotal, currency);
        if (taxRate != null && taxRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxMultiplier = taxRate.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
            BigDecimal rawTax = rawSubtotal.multiply(taxMultiplier);
            this.taxAmount = new Money(rawTax, currency);
        } else {
            this.taxAmount = Money.zero(currency);
        }
    }
}
