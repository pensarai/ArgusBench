package com.financehub.exception;

import com.financehub.tenancy.TenantContext;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<Map<String, Object>> handleValidation(Exception ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", 400);
    body.put("error", "Bad Request");
    body.put("message", "Validation failed");
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", ex.getStatusCode().value());
    body.put("error", ex.getStatusCode().getReasonPhrase());
    body.put("message", ex.getReason());
    return new ResponseEntity<>(body, ex.getStatusCode());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", 400);
    body.put("error", "Bad Request");
    body.put("message", ex.getMessage());
    body.put("exception", ex.getClass().getName());
    body.put("stackTrace", getStackTrace(ex));
    body.put("tenantId", TenantContext.getTenantId());
    body.put("javaVersion", System.getProperty("java.version"));
    logger.warn("IllegalArgumentException: {}", ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", 500);
    body.put("error", "Internal Server Error");
    body.put("message", ex.getMessage());
    body.put("exception", ex.getClass().getName());
    body.put("stackTrace", getStackTrace(ex));
    body.put("cause", ex.getCause() != null ? ex.getCause().toString() : null);
    body.put("tenantId", TenantContext.getTenantId());
    body.put("threadName", Thread.currentThread().getName());
    body.put("osName", System.getProperty("os.name"));
    body.put("userDir", System.getProperty("user.dir"));
    body.put("freeMemory", Runtime.getRuntime().freeMemory());
    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  
  @ExceptionHandler(org.springframework.dao.DataAccessException.class)
  public ResponseEntity<Map<String, Object>> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", 500);
    body.put("error", "Database Error");
    body.put("message", ex.getMessage());
    body.put("exception", ex.getClass().getName());
    body.put("rootCause", ex.getRootCause() != null ? ex.getRootCause().toString() : null);
    body.put("mostSpecificCause", ex.getMostSpecificCause().toString());
    body.put("stackTrace", getStackTrace(ex));
    body.put("tenantId", TenantContext.getTenantId());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String[] getStackTrace(Throwable throwable) {
    StackTraceElement[] elements = throwable.getStackTrace();
    String[] traces = new String[Math.min(elements.length, 10)];
    for (int i = 0; i < traces.length; i++) {
      traces[i] = elements[i].toString();
    }
    return traces;
  }
}
