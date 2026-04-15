package com.ecommerce.service;

import com.ecommerce.entity.AuditLog;
import com.ecommerce.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void log(String entityType, UUID entityId, String action, Object changes) {
        try {
            String email = (String) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            // TODO: Получить реального пользователя по email

            UUID userId = UUID.randomUUID();

            String changesJson = objectMapper.writeValueAsString(changes);

            AuditLog auditLog = new AuditLog();
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setAction(action);
            auditLog.setUserId(userId);
            auditLog.setChanges(changesJson);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);

            log.debug("Audit log created: {} - {} - {}", entityType, entityId, action);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }
}