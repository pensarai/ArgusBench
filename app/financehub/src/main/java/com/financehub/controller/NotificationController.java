package com.financehub.controller;

import com.financehub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<Page<com.financehub.entity.Notification>> list(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", required = false) String sort
  ) {
    return ResponseEntity.ok(notificationService.list(page, size, sort));
  }

  @GetMapping("/ping")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
  public ResponseEntity<?> pingNotify() {
    Long id = notificationService.notify(null, "PING", "{\"msg\":\"hello\"}");
    return ResponseEntity.ok(java.util.Map.of("id", id));
  }
  
  @PostMapping("/render-template")
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public ResponseEntity<?> renderTemplate(@RequestBody java.util.Map<String, Object> request) {
    String template = (String) request.get("template");
    java.util.Map<String, Object> data = (java.util.Map<String, Object>) request.get("data");
    
    if (template == null) {
      return ResponseEntity.badRequest().body("Template required");
    }
    
    if (data == null) {
      data = new java.util.HashMap<>();
    }
    
    String rendered = notificationService.renderNotificationTemplate(template, data);
    return ResponseEntity.ok(java.util.Map.of("rendered", rendered));
  }
}


