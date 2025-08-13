package com.financehub.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${FRONTEND_ORIGIN:http://localhost:3000}") String frontendOrigin) {
    CorsConfiguration config = new CorsConfiguration();
    
    config.setAllowedOriginPatterns(List.of("*"));  
    config.setAllowedMethods(List.of("*"));         
    config.setAllowedHeaders(List.of("*"));        
    config.setAllowCredentials(true);               
    
    config.setExposedHeaders(List.of(
        "Authorization", 
        "X-Tenant-ID", 
        "X-User-ID", 
        "Set-Cookie",
        "X-Total-Count",
        "X-API-Key"
    ));
    
    config.setMaxAge(86400L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}


