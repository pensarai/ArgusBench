package com.financehub.controller;

import com.financehub.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {
    
    private final WebhookService webhookService;
    
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> sendWebhook(@RequestBody Map<String, Object> request) {
        String webhookUrl = (String) request.get("url");
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Webhook URL required");
        }
        
        try {
            String response = webhookService.sendWebhook(webhookUrl, payload);
            return ResponseEntity.ok(Map.of(
                "message", "Webhook sent successfully",
                "response", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> validateWebhook(@RequestBody Map<String, String> request) {
        String webhookUrl = request.get("url");
        
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Webhook URL required");
        }
        
        try {
            String result = webhookService.validateWebhookEndpoint(webhookUrl);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> fetchConfig(@RequestParam("configUrl") String configUrl) {
        if (configUrl == null || configUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Config URL required");
        }
        
        try {
            String config = webhookService.fetchWebhookConfig(configUrl);
            return ResponseEntity.ok(Map.of("config", config));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/proxy")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<?> proxyRequest(@RequestBody Map<String, Object> request) {
        String targetUrl = (String) request.get("url");
        String method = (String) request.get("method");
        Map<String, String> headers = (Map<String, String>) request.get("headers");
        String body = (String) request.get("body");
        
        if (targetUrl == null || targetUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Target URL required");
        }
        
        if (method == null || method.isEmpty()) {
            method = "GET";
        }
        
        try {
            String response = webhookService.proxyRequest(targetUrl, method, headers, body);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/notify")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> request) {
        String notificationUrl = (String) request.get("notificationUrl");
        String message = (String) request.get("message");
        String userId = (String) request.get("userId");
        
        if (notificationUrl == null || notificationUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Notification URL required");
        }
        
        Map<String, Object> payload = Map.of(
            "message", message != null ? message : "Default notification",
            "userId", userId != null ? userId : "anonymous",
            "timestamp", java.time.Instant.now().toString()
        );
        
        try {
            String response = webhookService.sendWebhook(notificationUrl, payload);
            return ResponseEntity.ok(Map.of(
                "message", "Notification sent",
                "response", response
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}