package com.orvion.notification.presentation.controller;

import com.orvion.common.exception.ResourceNotFoundException;
import com.orvion.notification.domain.model.NotificationTemplate;
import com.orvion.notification.domain.repository.NotificationTemplateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/templates")
@Tag(name = "Notification Templates", description = "Template management for SUPER_ADMIN")
public class NotificationTemplateController extends BaseController {

    private final NotificationTemplateRepository templateRepository;

    public NotificationTemplateController(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @GetMapping
    @Operation(summary = "List all templates")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NOTIFICATION_MANAGER')")
    public List<NotificationTemplate> getTemplates() {
        String tenantId = extractTenantId();
        return templateRepository.findByTenantId(tenantId);
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "Get template by ID")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'NOTIFICATION_MANAGER')")
    public NotificationTemplate getTemplate(@PathVariable UUID templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new template")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public NotificationTemplate createTemplate(@Valid @RequestBody CreateTemplateRequest request) {
        NotificationTemplate template = new NotificationTemplate(
                extractTenantId(),
                request.templateCode(),
                request.subject(),
                request.body(),
                request.channel(),
                request.eventType(),
                request.language() != null ? request.language() : "en"
        );
        return templateRepository.save(template);
    }

    @PutMapping("/{templateId}")
    @Operation(summary = "Update a template")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public NotificationTemplate updateTemplate(@PathVariable UUID templateId,
                                                @Valid @RequestBody UpdateTemplateRequest request) {
        NotificationTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found: " + templateId));
        if (request.subject() != null) template.setSubject(request.subject());
        if (request.body() != null) template.setBody(request.body());
        if (request.channel() != null) template.setChannel(request.channel());
        if (request.active() != null) template.setActive(request.active());
        return templateRepository.save(template);
    }

    @DeleteMapping("/{templateId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a template")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void deleteTemplate(@PathVariable UUID templateId) {
        templateRepository.deleteById(templateId);
    }

    public record CreateTemplateRequest(
            @NotBlank String templateCode,
            @NotBlank String subject,
            @NotBlank String body,
            @NotNull com.orvion.notification.domain.model.enums.NotificationChannel channel,
            @NotBlank String eventType,
            String language
    ) {}

    public record UpdateTemplateRequest(
            String subject,
            String body,
            com.orvion.notification.domain.model.enums.NotificationChannel channel,
            Boolean active
    ) {}
}
