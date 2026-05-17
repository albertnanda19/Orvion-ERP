package com.orvion.sales.domain.model;

import com.orvion.common.exception.BusinessException;
import com.orvion.sales.domain.model.enums.OpportunityStage;
import com.orvion.sales.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityTest {

    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        opportunity = new Opportunity("tenant1", "Enterprise Deal", null, "acc-001", "salesrep1");
    }

    @Test
    void testCreateOpportunity() {
        assertNotNull(opportunity.getId());
        assertEquals("tenant1", opportunity.getTenantId());
        assertEquals("Enterprise Deal", opportunity.getTitle());
        assertEquals(OpportunityStage.DISCOVERY, opportunity.getStage());
        assertEquals(10, opportunity.getProbability());
        assertTrue(opportunity.getExpectedValue().isZero());
    }

    @Test
    void testAdvanceStageDiscoveryToProposal() {
        opportunity.advanceStage();
        assertEquals(OpportunityStage.PROPOSAL, opportunity.getStage());
        assertEquals(40, opportunity.getProbability());
    }

    @Test
    void testAdvanceStageProposalToNegotiation() {
        opportunity.advanceStage();
        opportunity.advanceStage();
        assertEquals(OpportunityStage.NEGOTIATION, opportunity.getStage());
        assertEquals(70, opportunity.getProbability());
    }

    @Test
    void testAdvanceStageToClosedWon() {
        opportunity.advanceStage();
        opportunity.advanceStage();
        opportunity.advanceStage();
        assertEquals(OpportunityStage.CLOSED_WON, opportunity.getStage());
        assertEquals(100, opportunity.getProbability());
    }

    @Test
    void testAdvanceClosedOpportunity() {
        opportunity.advanceStage();
        opportunity.advanceStage();
        opportunity.advanceStage();
        assertThrows(BusinessException.class, () -> opportunity.advanceStage());
    }

    @Test
    void testCloseWon() {
        Money actualValue = new Money(new BigDecimal("50000000"), "IDR");
        opportunity.closeWon(actualValue);
        assertEquals(OpportunityStage.CLOSED_WON, opportunity.getStage());
        assertEquals(0, actualValue.getAmount().compareTo(opportunity.getExpectedValue().getAmount()));
    }

    @Test
    void testCloseLost() {
        opportunity.closeLost("Price too high");
        assertEquals(OpportunityStage.CLOSED_LOST, opportunity.getStage());
        assertEquals(0, opportunity.getProbability());
    }

    @Test
    void testCloseWonAlreadyClosedLost() {
        opportunity.closeLost("Budget issues");
        assertThrows(BusinessException.class, () -> opportunity.closeWon(null));
    }

    @Test
    void testCloseLostAlreadyClosedWon() {
        opportunity.closeWon(null);
        assertThrows(BusinessException.class, () -> opportunity.closeLost("Reason"));
    }
}
