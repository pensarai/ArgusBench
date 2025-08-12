package com.financehub.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

  private static final int LIMIT = 100; // 100 req / minute per IP
  private static final long WINDOW_TTL_SECONDS = 5 * 60; // 5 minutes
  private static final Map<String, Window> WINDOWS = new ConcurrentHashMap<>();
  private static volatile long lastCleanup = 0;
  private static final long CLEANUP_INTERVAL_SECONDS = 30; // Run cleanup at most every 30 seconds

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String ip = request.getRemoteAddr();
    long now = Instant.now().getEpochSecond();
    long minute = now / 60;
    maybeCleanupOldWindows(now);
    Window w = WINDOWS.computeIfAbsent(ip, k -> new Window(minute, now));
    synchronized (w) {
      if (w.minute != minute) {
        w.minute = minute;
        w.count.set(0);
      }
      w.lastAccess = now;
      if (w.count.incrementAndGet() > LIMIT) {
        response.setStatus(429);
        response.getWriter().write("Rate limit exceeded");
        return;
      }
    }
    filterChain.doFilter(request, response);
  }

  // Remove windows that have not been accessed for WINDOW_TTL_SECONDS, but only run every CLEANUP_INTERVAL_SECONDS
  private static void maybeCleanupOldWindows(long now) {
    if (now - lastCleanup < CLEANUP_INTERVAL_SECONDS) {
      return;
    }
    lastCleanup = now;
    Iterator<Map.Entry<String, Window>> it = WINDOWS.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Window> entry = it.next();
      Window w = entry.getValue();
      if (now - w.lastAccess > WINDOW_TTL_SECONDS) {
        it.remove();
      }
    }
  }

  static class Window {
    volatile long minute;
    AtomicInteger count = new AtomicInteger();
    volatile long lastAccess;
    Window(long m, long now) { this.minute = m; this.lastAccess = now; }
  }
}
