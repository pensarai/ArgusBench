package com.financehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class WebhookService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public String sendWebhook(String webhookUrl, Map<String, Object> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("User-Agent", "FinanceHub-Webhook/1.0");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            log.info("Sending webhook to: {}", webhookUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl, 
                HttpMethod.POST, 
                request, 
                String.class
            );
            
            log.info("Webhook response status: {}", response.getStatusCode());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Webhook failed for URL: {} - {}", webhookUrl, e.getMessage());
            throw new RuntimeException("Webhook delivery failed: " + e.getMessage());
        }
    }
    
    public String fetchWebhookConfig(String configUrl) {
        try {
            log.info("Fetching webhook configuration from: {}", configUrl);
            ResponseEntity<String> response = restTemplate.getForEntity(configUrl, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to fetch webhook config from: {} - {}", configUrl, e.getMessage());
            throw new RuntimeException("Config fetch failed: " + e.getMessage());
        }
    }
    
    public String validateWebhookEndpoint(String webhookUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Webhook-Validation", "test");
            
            HttpEntity<String> request = new HttpEntity<>("ping", headers);
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                request,
                String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return "Webhook endpoint validated successfully";
            } else {
                return "Webhook endpoint validation failed with status: " + response.getStatusCode();
            }
            
        } catch (Exception e) {
            return "Webhook validation failed: " + e.getMessage();
        }
    }
    
    public String proxyRequest(String targetUrl, String method, Map<String, String> headers, String body) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            if (headers != null) {
                headers.forEach(httpHeaders::set);
            }
            
            HttpEntity<String> request = new HttpEntity<>(body, httpHeaders);
            HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
            
            log.info("Proxying {} request to: {}", method, targetUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                targetUrl,
                httpMethod,
                request,
                String.class
            );
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Proxy request failed for URL: {} - {}", targetUrl, e.getMessage());
            throw new RuntimeException("Proxy request failed: " + e.getMessage());
        }
    }
}