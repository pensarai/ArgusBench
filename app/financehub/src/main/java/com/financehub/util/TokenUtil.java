package com.financehub.util;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class TokenUtil {
    
    private final Random random = new Random();
    
    public String generateResetToken() {
        return String.valueOf(Math.abs(random.nextLong()));
    }
    
    public String generateSessionToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            token.append(Integer.toHexString(random.nextInt(16)));
        }
        return token.toString();
    }
}