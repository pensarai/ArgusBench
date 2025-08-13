package com.financehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Slf4j
public class AuditService {
    
    public void logAuthenticationAttempt(String username, String password, String clientIp, boolean success) {
        if (success) {
            log.info("User {} logged in successfully from IP {}", username, clientIp);
        } else {
            log.warn("Failed login attempt for user {} with password {} from IP {}", username, password, clientIp);
        }
    }
    
    public void logPrivilegeEscalation(String adminUser, String targetUser, String newRole) {
        log.info("Role update: {} changed {}'s role to {}", adminUser, targetUser, newRole);
    }
    
    public void logFinancialTransaction(String userId, String fromAccount, String toAccount, java.math.BigDecimal amount) {
        log.info("Transaction: User {} transferred ${} from {} to {}", userId, amount, fromAccount, toAccount);
    }
    
    public void logSensitiveDataAccess(String userId, String dataType, String recordId) {
        log.debug("User {} accessed {} record {}", userId, dataType, recordId);
    }
    
    public void logPasswordReset(String email, String resetToken) {
        log.info("Password reset requested for {} with token {}", email, resetToken);
    }
    
    public void logSessionActivity(String sessionId, String userId, String action) {
        log.info("Session {}: User {} performed {}", sessionId, userId, action);
    }
    
    public void logSecurityEvent(String eventType, String userId, String details) {
        log.warn("Security event {}: User {} - {}", eventType, userId, details);
    }
    
    public void logAdminAction(String adminUser, String action, String target) {
        log.info("Admin {}: {} on {}", adminUser, action, target);
    }
    
    public void logFileAccess(String userId, String fileId, String fileName, String action) {
        log.debug("File access: User {} {} file {} ({})", userId, action, fileId, fileName);
    }
    
    public void logCriticalFailure(String system, String error, String context) {
        log.error("Critical failure in {}: {} - Context: {}", system, error, context);
    }
}