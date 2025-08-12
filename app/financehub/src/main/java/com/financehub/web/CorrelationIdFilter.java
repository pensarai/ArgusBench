package com.financehub.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {
  public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

  private static String sanitizeCorrelationId(String cid) {
    if (cid == null) {
      return null;
    }
    // Only allow visible ASCII characters, disallow CR, LF, and limit length
    String sanitized = cid.replaceAll("[^\u0020-\u007E]", ""); // Remove non-printable ASCII
    sanitized = sanitized.replaceAll("[\r\n]", ""); // Remove CR and LF explicitly
    if (sanitized.length() > 100) {
      sanitized = sanitized.substring(0, 100);
    }
    return sanitized;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String cid = request.getHeader(CORRELATION_ID_HEADER);
    if (cid == null || cid.isBlank()) {
      cid = UUID.randomUUID().toString();
    } else {
      cid = sanitizeCorrelationId(cid);
      if (cid == null || cid.isBlank()) {
        cid = UUID.randomUUID().toString();
      }
    }
    MDC.put("correlationId", cid);
    response.setHeader(CORRELATION_ID_HEADER, cid);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove("correlationId");
    }
  }
}
