package com.orvion.sales.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.LeadSource;
import com.orvion.sales.domain.model.enums.LeadStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeadTest {

    private Lead lead;

    @BeforeEach
    void setUp() {
        lead = new Lead("tenant1", "John", "Doe", "john@test.com",
            "0812-3456", "ACME Corp", LeadSource.WEBSITE, "salesrep1");
    }

    @Test
    void testCreateLead() {
        assertNotNull(lead.getId());
        assertEquals("tenant1", lead.getTenantId());
        assertEquals("John", lead.getFirstName());
        assertEquals("Doe", lead.getLastName());
        assertEquals(LeadSource.WEBSITE, lead.getSource());
        assertEquals(LeadStatus.NEW, lead.getStatus());
        assertEquals("salesrep1", lead.getAssignedTo());
    }

    @Test
    void testQualifyLead() {
        lead.qualify();
        assertEquals(LeadStatus.QUALIFIED, lead.getStatus());
    }

    @Test
    void testQualifyDisqualifiedLead() {
        lead.disqualify("Not interested");
        assertThrows(BusinessException.class, () -> lead.qualify());
    }

    @Test
    void testDisqualifyLead() {
        lead.disqualify("Budget too low");
        assertEquals(LeadStatus.DISQUALIFIED, lead.getStatus());
        assertEquals("Budget too low", lead.getNotes());
    }

    @Test
    void testDisqualifyAlreadyDisqualified() {
        lead.disqualify("Not interested");
        assertThrows(BusinessException.class, () -> lead.disqualify("Other reason"));
    }

    @Test
    void testConvertToOpportunity() {
        lead.qualify();
        Opportunity opp = lead.convertToOpportunity("New Software Deal", "salesrep1");
        assertNotNull(opp);
        assertEquals("tenant1", opp.getTenantId());
        assertEquals("New Software Deal", opp.getTitle());
        assertEquals(lead.getId(), opp.getLeadId());
    }

    @Test
    void testConvertNonQualifiedLead() {
        assertThrows(BusinessException.class, () -> lead.convertToOpportunity("Deal", "rep1"));
    }
}
