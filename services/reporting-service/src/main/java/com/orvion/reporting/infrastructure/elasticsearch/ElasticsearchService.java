package com.orvion.reporting.infrastructure.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Instant;
import java.util.*;

@Service
public class ElasticsearchService {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public static final String AUDIT_LOGS_INDEX = "orvion-audit-logs";

    public ElasticsearchService(@Value("${spring.elasticsearch.uris}") String esUri) {
        this.webClient = WebClient.builder().baseUrl(esUri).build();
        this.objectMapper = new ObjectMapper();
    }

    public void indexAuditLog(String id, Map<String, Object> document) {
        try {
            String response = webClient.put()
                .uri("/" + AUDIT_LOGS_INDEX + "/_doc/" + id)
                .bodyValue(document)
                .retrieve()
                .bodyToMono(String.class)
                .block();
            log.debug("Indexed audit log {}: {}", id, response);
        } catch (Exception e) {
            log.error("Failed to index audit log {}: {}", id, e.getMessage());
        }
    }

    public List<Map<String, Object>> searchAuditLogs(String tenantId, String query, Instant startDate,
                                                      Instant endDate, Pageable pageable) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("size", pageable.getPageSize());
            body.put("from", (int) pageable.getOffset());

            List<Map<String, Object>> mustClauses = new ArrayList<>();
            mustClauses.add(Map.of("term", Map.of("tenantId", tenantId)));

            if (query != null && !query.isBlank()) {
                mustClauses.add(Map.of("multi_match", Map.of(
                    "query", query,
                    "fields", List.of("action", "details", "userId", "serviceName")
                )));
            }

            if (startDate != null || endDate != null) {
                Map<String, Object> range = new HashMap<>();
                if (startDate != null) range.put("gte", startDate.toString());
                if (endDate != null) range.put("lte", endDate.toString());
                mustClauses.add(Map.of("range", Map.of("timestamp", range)));
            }

            body.put("query", Map.of("bool", Map.of("must", mustClauses)));
            body.put("sort", List.of(Map.of("timestamp", Map.of("order", "desc"))));

            String response = webClient.post()
                .uri("/orvion-audit-logs/_search")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response == null) return Collections.emptyList();

            JsonNode root = objectMapper.readTree(response);
            JsonNode hits = root.path("hits").path("hits");
            List<Map<String, Object>> results = new ArrayList<>();
            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                Map<String, Object> map = objectMapper.treeToValue(source, Map.class);
                results.add(map);
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to search audit logs: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> aggregateSalesByPeriod(String tenantId, Instant startDate, Instant endDate) {
        try {
            Map<String, Object> body = new HashMap<>();

            Map<String, Object> rangeFilter = new HashMap<>();
            rangeFilter.put("range", Map.of("timestamp", Map.of(
                "gte", startDate.toString(),
                "lte", endDate.toString()
            )));

            Map<String, Object> termFilter = new HashMap<>();
            termFilter.put("term", Map.of("tenantId", tenantId));

            body.put("query", Map.of("bool", Map.of("filter", List.of(termFilter, rangeFilter))));
            body.put("size", 0);

            Map<String, Object> dateHistogram = new HashMap<>();
            dateHistogram.put("field", "timestamp");
            dateHistogram.put("calendar_interval", "month");
            dateHistogram.put("format", "yyyy-MM");

            Map<String, Object> aggs = new HashMap<>();
            aggs.put("sales_over_time", Map.of("date_histogram", dateHistogram));

            Map<String, Object> revenueStats = new HashMap<>();
            revenueStats.put("field", "total");
            aggs.put("total_revenue", Map.of("sum", revenueStats));

            body.put("aggs", aggs);

            String response = webClient.post()
                .uri("/orvion-sales-orders/_search")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response == null) return Collections.emptyMap();
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.error("Failed to aggregate sales: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    public List<Map<String, Object>> getTopProducts(String tenantId, int limit) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("size", 0);

            body.put("query", Map.of("term", Map.of("tenantId", tenantId)));

            Map<String, Object> termsAgg = new HashMap<>();
            termsAgg.put("field", "productName.keyword");
            termsAgg.put("size", limit);
            termsAgg.put("order", Map.of("total_revenue", "desc"));

            Map<String, Object> revenueSubAgg = new HashMap<>();
            revenueSubAgg.put("field", "total");
            Map<String, Object> aggs = new HashMap<>();
            aggs.put("top_products", Map.of("terms", termsAgg,
                "aggs", Map.of("total_revenue", Map.of("sum", revenueSubAgg))));
            body.put("aggs", aggs);

            String response = webClient.post()
                .uri("/orvion-sales-orders/_search")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            if (response == null) return Collections.emptyList();

            JsonNode root = objectMapper.readTree(response);
            JsonNode buckets = root.path("aggregations").path("top_products").path("buckets");
            List<Map<String, Object>> results = new ArrayList<>();
            for (JsonNode bucket : buckets) {
                Map<String, Object> item = new HashMap<>();
                item.put("productName", bucket.path("key").asText());
                item.put("count", bucket.path("doc_count").asLong());
                item.put("totalRevenue", bucket.path("total_revenue").path("value").asDouble());
                results.add(item);
            }
            return results;
        } catch (Exception e) {
            log.error("Failed to get top products: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
