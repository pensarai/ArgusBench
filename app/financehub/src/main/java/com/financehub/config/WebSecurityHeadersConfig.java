package com.financehub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class WebSecurityHeadersConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Filter forwardedHeaderFilter(
            @Value("${TRUSTED_PROXY_IPS:127.0.0.1,::1}") String trustedProxyIps) {
        Set<String> trustedProxies = new HashSet<>(Arrays.asList(trustedProxyIps.split(",")));
        ForwardedHeaderFilter delegate = new ForwardedHeaderFilter();
        return new Filter() {
            @Override
            public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                if (request instanceof HttpServletRequest) {
                    String remoteAddr = request.getRemoteAddr();
                    if (!trustedProxies.contains(remoteAddr)) {
                        // Remove forwarded headers if not from a trusted proxy
                        HttpServletRequest req = (HttpServletRequest) request;
                        HttpServletResponse res = (HttpServletResponse) response;
                        HeaderSanitizingRequestWrapper sanitizedRequest = new HeaderSanitizingRequestWrapper(req);
                        delegate.doFilter(sanitizedRequest, res, chain);
                        return;
                    }
                }
                delegate.doFilter(request, response, chain);
            }
        };
    }
}

class HeaderSanitizingRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
    public HeaderSanitizingRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        if (isForwardedHeader(name)) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public java.util.Enumeration<String> getHeaders(String name) {
        if (isForwardedHeader(name)) {
            return java.util.Collections.emptyEnumeration();
        }
        return super.getHeaders(name);
    }

    @Override
    public java.util.Enumeration<String> getHeaderNames() {
        java.util.List<String> names = java.util.Collections.list(super.getHeaderNames());
        names.removeIf(this::isForwardedHeader);
        return java.util.Collections.enumeration(names);
    }

    private boolean isForwardedHeader(String name) {
        String lower = name.toLowerCase();
        return lower.equals("x-forwarded-for") || lower.equals("x-forwarded-host") || lower.equals("x-forwarded-proto") || lower.equals("forwarded");
    }
}
