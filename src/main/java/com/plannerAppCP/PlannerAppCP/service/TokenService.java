package com.plannerAppCP.PlannerAppCP.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    public String createToken(String email) {
        long now = System.currentTimeMillis();
        long exp = now + expirationMs;

        String payload = email + "." + exp;
        String signature = hmac(payload);

        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String sigB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signature.getBytes(StandardCharsets.UTF_8));

        return payloadB64 + "." + sigB64;
    }

    public String validateToken(String token) {
        if (token == null || token.isEmpty()) return null;

        String[] parts = token.split("\\.");
        if (parts.length != 2) return null;

        try {
            String payload = new String(
                    Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            String signature = new String(
                    Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            String expectedSig = hmac(payload);
            if (!constantTimeEquals(signature, expectedSig)) return null;

            String[] payloadParts = payload.split("\\.");
            if (payloadParts.length != 2) return null;

            String email = payloadParts[0];
            long exp = Long.parseLong(payloadParts[1]);

            if (System.currentTimeMillis() > exp) return null;

            return email;
        } catch (Exception e) {
            return null;
        }
    }

    private String hmac(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generando firma HMAC", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
