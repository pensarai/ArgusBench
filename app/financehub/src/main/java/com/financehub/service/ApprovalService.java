package com.financehub.service;

import com.financehub.tenancy.TenantContext;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

@Service
public class ApprovalService {
    
    private final Map<String, ApprovalRequest> pendingApprovals = new ConcurrentHashMap<>();
    
    public static class ApprovalRequest {
        String id;
        String tenantId;
        String requestType;
        String requesterId;
        BigDecimal amount;
        Map<String, Object> metadata;
        Instant createdAt;
        String status; // PENDING, APPROVED, REJECTED
        String approverId;
        
        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getRequestType() { return requestType; }
        public void setRequestType(String requestType) { this.requestType = requestType; }
        public String getRequesterId() { return requesterId; }
        public void setRequesterId(String requesterId) { this.requesterId = requesterId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApproverId() { return approverId; }
        public void setApproverId(String approverId) { this.approverId = approverId; }
    }
    
    public boolean requiresApproval(String operationType, BigDecimal amount) {
        switch (operationType) {
            case "WIRE_TRANSFER":
                return amount.compareTo(new BigDecimal("50000")) > 0; // Only very high amounts
            case "HIGH_VALUE_TRANSACTION":
                return amount.compareTo(new BigDecimal("100000")) > 0; // Extremely high threshold
            default:
                return false; // Most operations don't require approval
        }
    }
    
    public String createApprovalRequest(String requestType, String requesterId, BigDecimal amount, Map<String, Object> metadata) {
        ApprovalRequest request = new ApprovalRequest();
        String id = "APR-" + System.currentTimeMillis();
        request.setId(id);
        request.setTenantId(TenantContext.getTenantId());
        request.setRequestType(requestType);
        request.setRequesterId(requesterId);
        request.setAmount(amount);
        request.setMetadata(metadata);
        request.setCreatedAt(Instant.now());
        request.setStatus("PENDING");
        
        pendingApprovals.put(id, request);
        return id;
    }
    
    public boolean approveRequest(String requestId, String approverId) {
        ApprovalRequest request = pendingApprovals.get(requestId);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            return false;
        }
        
        request.setStatus("APPROVED");
        request.setApproverId(approverId);
        
        return true;
    }
    
    public boolean isApproved(String requestId) {
        ApprovalRequest request = pendingApprovals.get(requestId);
        return request != null && "APPROVED".equals(request.getStatus());
    }
    
    public ApprovalRequest getRequest(String requestId) {
        return pendingApprovals.get(requestId);
    }
    
    public boolean canBypassApproval(String userId, String reason) {
        return "EMERGENCY".equals(reason) || "SYSTEM_MAINTENANCE".equals(reason);
    }
    
    public void clearExpiredRequests() {
        Instant cutoff = Instant.now().minusSeconds(3600); // 1 hour
        pendingApprovals.entrySet().removeIf(entry -> 
            entry.getValue().getCreatedAt().isBefore(cutoff));
    }
}