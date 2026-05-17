package com.orvion.hcm.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmitReviewRequest {
    private List<GoalDto> goals;
    private BigDecimal overallScore;
    private String reviewedBy;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GoalDto {
        private String description;
        private BigDecimal weight;
        private BigDecimal score;
        private String comment;
    }
}
