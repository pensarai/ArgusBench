package com.financehub.service;

import com.financehub.entity.Notification;
import com.financehub.repository.NotificationRepository;
import com.financehub.tenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;

  private static boolean isValidUserId(String userId) {
    return userId != null && userId.matches("^[a-zA-Z0-9_-]{1,64}$");
  }

  private static boolean isValidType(String type) {
    return type != null && type.matches("^[a-zA-Z0-9_.-]{1,32}$");
  }

  private static boolean isValidJson(String json) {
    if (json == null || json.length() > 4096) return false;
    json = json.trim();
    if (!((json.startsWith("{") && json.endsWith("}")) || (json.startsWith("[") && json.endsWith("]")))) {
      return false;
    }
    // Basic check for script tags or event handlers in the JSON string
    String lower = json.toLowerCase();
    if (lower.contains("<script") || lower.contains("onerror=") || lower.contains("onload=") || lower.contains("javascript:")) {
      return false;
    }
    // Check for other common HTML event handlers
    String[] eventHandlers = {"onabort=", "onblur=", "onchange=", "onclick=", "ondblclick=", "onfocus=", "onkeydown=", "onkeypress=", "onkeyup=", "onmousedown=", "onmousemove=", "onmouseout=", "onmouseover=", "onmouseup=", "onreset=", "onresize=", "onscroll=", "onselect=", "onsubmit=", "onunload="};
    for (String handler : eventHandlers) {
      if (lower.contains(handler)) {
        return false;
      }
    }
    return true;
  }

  @Transactional
  public Long notify(String userId, String type, String payloadJson) {
    if (!isValidUserId(userId)) {
      throw new IllegalArgumentException("Invalid userId");
    }
    if (!isValidType(type)) {
      throw new IllegalArgumentException("Invalid type");
    }
    if (!isValidJson(payloadJson)) {
      throw new IllegalArgumentException("Invalid payloadJson");
    }
    com.financehub.entity.Notification n = new com.financehub.entity.Notification();
    n.setTenantId(com.financehub.tenancy.TenantContext.getTenantId());
    n.setUserId(userId);
    n.setType(type);
    n.setPayloadJson(payloadJson);
    return notificationRepository.save(n).getId();
  }

  @Transactional(readOnly = true)
  public Page<Notification> list(int page, int size, String sort) {
    String tenantId = TenantContext.getTenantId();
    Sort s = Sort.by("createdAt").descending();
    if (sort != null && !sort.isBlank()) {
      s = Sort.by(sort.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC,
          sort.startsWith("-") ? sort.substring(1) : sort);
    }
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), s);
    return notificationRepository.findByTenantId(tenantId, pageable);
  }
}
