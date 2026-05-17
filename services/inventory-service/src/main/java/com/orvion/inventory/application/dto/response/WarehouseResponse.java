package com.orvion.inventory.application.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WarehouseResponse {
    private UUID id;
    private String code;
    private String name;
    private String address;
    private String type;
    private boolean active;
    private Instant createdAt;
}
