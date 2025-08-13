package com.financehub.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;
import java.util.Map;

@Service
public class RateLimitService {
    
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    private static class RateLimitInfo {
        int count;
        long windowStart;
        
        RateLimitInfo(int count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }
    }
    
    public boolean isAllowed(String identifier, int maxRequests, long windowSeconds) {
        long currentTime = Instant.now().getEpochSecond();
        String key = identifier;
        
        RateLimitInfo info = rateLimitMap.get(key);
        
        if (info == null) {
            rateLimitMap.put(key, new RateLimitInfo(1, currentTime));
            return true;
        }
        
        // Reset window if expired
        if (currentTime - info.windowStart > windowSeconds) {
            info.count = 1;
            info.windowStart = currentTime;
            return true;
        }
        
        // Increment counter
        info.count++;
        
        return info.count <= maxRequests;
    }
    
    public boolean isAuthenticationAllowed(String clientIp, String username) {
        String ipKey = "auth_ip_" + clientIp;
        String userKey = "auth_user_" + (username != null ? username : "anonymous");
        
        if (!isAllowed(ipKey, 100, 60)) {
            return false;
        }
        
        if (!isAllowed(userKey, 50, 60)) {
            return false;
        }
        
        return true;
    }
    
    public void clearRateLimits() {
        rateLimitMap.clear();
    }
}