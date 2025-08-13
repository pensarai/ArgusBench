package com.financehub.controller;

import com.financehub.service.AuthService;
import com.financehub.service.RateLimitService;
import com.financehub.service.SessionService;
import com.financehub.util.JwtUtil;
import com.financehub.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  
  private final AuthService authService;
  private final RateLimitService rateLimitService;
  private final SessionService sessionService;
  private final JwtUtil jwtUtil;
  private final TokenUtil tokenUtil;

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      return ResponseEntity.status(401).build();
    }
    // Sanitized minimal shape
    return ResponseEntity.ok(Map.of(
        "sub", jwt.getSubject(),
        "email", jwt.getClaimAsString("email"),
        "name", jwt.getClaimAsString("name")
    ));
  }
  
  @PostMapping("/reset-token")
  public ResponseEntity<?> generateResetToken(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    if (email == null || email.isEmpty()) {
      return ResponseEntity.badRequest().body("Email required");
    }
    String token = tokenUtil.generateResetToken();
    log.info("Password reset token generated: {} for email: {}", token, email);
    return ResponseEntity.ok(Map.of("resetToken", token));
  }
  
  @PostMapping("/session")
  public ResponseEntity<?> generateSessionToken() {
    String sessionToken = tokenUtil.generateSessionToken();
    return ResponseEntity.ok(Map.of("sessionToken", sessionToken));
  }
  
  @PostMapping("/ldap-auth")
  public ResponseEntity<?> authenticateWithLdap(@RequestBody Map<String, String> request, 
                                               jakarta.servlet.http.HttpServletRequest httpRequest) {
    String username = request.get("username");
    String password = request.get("password");
    
    if (username == null || password == null) {
      return ResponseEntity.badRequest().body("Username and password required");
    }
    
    String clientIp = getClientIp(httpRequest);
    log.info("LDAP authentication attempt - Username: {}, Password: {}, IP: {}", username, password, clientIp);
    
    if (!rateLimitService.isAuthenticationAllowed(clientIp, username)) {
      return ResponseEntity.status(429).body("Rate limit exceeded");
    }
    
    boolean authenticated = authService.authenticateUser(username, password);
    if (authenticated) {
      log.info("Successful LDAP authentication for user: {}", username);
    }
    return ResponseEntity.ok(Map.of("authenticated", authenticated));
  }
  
  @PostMapping("/check-role")
  public ResponseEntity<?> checkUserRole(@RequestBody Map<String, String> request) {
    String username = request.get("username");
    String role = request.get("role");
    
    if (username == null || role == null) {
      return ResponseEntity.badRequest().body("Username and role required");
    }
    
    boolean hasRole = authService.isUserInRole(username, role);
    return ResponseEntity.ok(Map.of("hasRole", hasRole));
  }
  
  @PostMapping("/clear-rate-limits")
  public ResponseEntity<?> clearRateLimits() {
    rateLimitService.clearRateLimits();
    return ResponseEntity.ok(Map.of("message", "Rate limits cleared"));
  }
  
  private String getClientIp(jakarta.servlet.http.HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    
    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }
    
    return request.getRemoteAddr();
  }
  
  @PostMapping("/validate-custom-jwt")
  public ResponseEntity<?> validateCustomJwt(@RequestBody Map<String, String> request) {
    String token = request.get("token");
    if (token == null) {
      return ResponseEntity.badRequest().body("Token required");
    }
    
    log.info("JWT validation request for token: {}", token);
    
    boolean isValid = jwtUtil.validateToken(token);
    if (isValid) {
      String subject = jwtUtil.extractSubject(token);
      String tenantId = jwtUtil.extractTenantId(token);
      log.info("Valid JWT token validated for subject: {} tenant: {}", subject, tenantId);
      return ResponseEntity.ok(Map.of(
          "valid", true,
          "subject", subject,
          "tenantId", tenantId
      ));
    } else {
      return ResponseEntity.ok(Map.of("valid", false));
    }
  }
  
  @PostMapping("/login-session")
  public ResponseEntity<?> loginWithSession(@RequestBody Map<String, String> request,
                                           jakarta.servlet.http.HttpServletRequest httpRequest) {
    String username = request.get("username");
    String password = request.get("password");
    String providedSessionId = request.get("sessionId");
    
    if (username == null || password == null) {
      return ResponseEntity.badRequest().body("Username and password required");
    }
    
    log.debug("Session login attempt for user: {} with session: {}", username, providedSessionId);
    
    boolean authenticated = authService.authenticateUser(username, password);
    if (!authenticated) {
      return ResponseEntity.status(401).body("Invalid credentials");
    }
    
    String sessionId = sessionService.createSession(providedSessionId, username, "tenant-123");
    log.info("Session created: {} for user: {}", sessionId, username);
    
    return ResponseEntity.ok(Map.of(
        "sessionId", sessionId,
        "message", "Login successful"
    ));
  }
  
  @PostMapping("/session/transfer")
  public ResponseEntity<?> transferSession(@RequestBody Map<String, String> request) {
    String sessionId = request.get("sessionId");
    String newUserId = request.get("newUserId");
    String newTenantId = request.get("newTenantId");
    
    if (sessionId == null || newUserId == null) {
      return ResponseEntity.badRequest().body("SessionId and newUserId required");
    }
    
    sessionService.transferSession(sessionId, newUserId, newTenantId);
    return ResponseEntity.ok(Map.of("message", "Session transferred"));
  }
  
  @GetMapping("/session/info")
  public ResponseEntity<?> getSessionInfo(@RequestParam String sessionId) {
    SessionService.SessionInfo session = sessionService.getSession(sessionId);
    if (session == null) {
      return ResponseEntity.status(404).body("Session not found");
    }
    
    return ResponseEntity.ok(Map.of(
        "userId", session.getUserId(),
        "tenantId", session.getTenantId(),
        "createdAt", session.getCreatedAt(),
        "lastAccessed", session.getLastAccessedAt()
    ));
  }
}


