package com.orvion.inventory.domain.event;
import com.orvion.common.event.DomainEvent;
import java.math.BigDecimal;

public class PurchaseOrderApprovedEvent extends DomainEvent {
    private final String poNumber;
    private final String supplierId;
    private final BigDecimal totalAmount;
    private final String approvedBy;
    
    public PurchaseOrderApprovedEvent(String poId, String poNumber, String supplierId, BigDecimal totalAmount, String approvedBy, String tenantId) {
        super("PO_APPROVED", "PurchaseOrder", poId, tenantId);
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.totalAmount = totalAmount;
        this.approvedBy = approvedBy;
    }
    
    public String getPoNumber() { return poNumber; }
    public String getSupplierId() { return supplierId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getApprovedBy() { return approvedBy; }
}
