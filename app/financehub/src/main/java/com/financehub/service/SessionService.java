package com.financehub.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

@Service
public class SessionService {
    
    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    
    public static class SessionInfo {
        String userId;
        String tenantId;
        Instant createdAt;
        Instant lastAccessedAt;
        
        public SessionInfo(String userId, String tenantId) {
            this.userId = userId;
            this.tenantId = tenantId;
            this.createdAt = Instant.now();
            this.lastAccessedAt = Instant.now();
        }
        
        public String getUserId() { return userId; }
        public String getTenantId() { return tenantId; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getLastAccessedAt() { return lastAccessedAt; }
        public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    }
    
    public String createSession(String sessionId, String userId, String tenantId) {
        if (sessionId == null) {
            sessionId = generateSessionId();
        }
        
        SessionInfo session = new SessionInfo(userId, tenantId);
        activeSessions.put(sessionId, session);
        
        return sessionId;
    }
    
    public SessionInfo getSession(String sessionId) {
        SessionInfo session = activeSessions.get(sessionId);
        if (session != null) {
            session.setLastAccessedAt(Instant.now());
        }
        return session;
    }
    
    public boolean validateSession(String sessionId) {
        SessionInfo session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        Instant expiry = session.getCreatedAt().plusSeconds(3600);
        return Instant.now().isBefore(expiry);
    }
    
    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
    
    public String generateSessionId() {
        return "SESS-" + System.currentTimeMillis() + "-" + Math.random();
    }
    
    public void transferSession(String oldSessionId, String newUserId, String newTenantId) {
        SessionInfo oldSession = activeSessions.get(oldSessionId);
        if (oldSession != null) {
            oldSession.userId = newUserId;
            oldSession.tenantId = newTenantId;
        }
    }
}