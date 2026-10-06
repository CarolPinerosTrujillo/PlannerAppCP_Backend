package com.plannerAppCP.PlannerAppCP.service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Envía el código de verificación por la API de Brevo (HTTPS).
 * Motivo: Render free bloquea el tráfico saliente SMTP (puertos 25/465/587),
 * por lo que usar Gmail SMTP falla siempre con "Connect timed out".
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_SEND_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${email.api-key:}")
    private String apiKey;

    @Value("${email.from:}")
    private String fromAddress;

    private RestClient restClient;

    private RestClient client() {
        if (restClient == null) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(Duration.ofSeconds(8));
            factory.setReadTimeout(Duration.ofSeconds(8));
            restClient = RestClient.builder().requestFactory(factory).build();
        }
        return restClient;
    }

    public boolean sendRecoveryCode(String to, String code) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("EMAIL_API_KEY no configurado → no se envía correo a {}", to);
            return false;
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sender", Map.of("name", "PlannerApp", "email", extractEmail(fromAddress)));
            payload.put("to", List.of(Map.of("email", to)));
            payload.put("subject", "PlannerApp - Tu código de verificación");
            payload.put("textContent",
                    "Hola,\n\n" +
                    "Tu código de verificación es: " + code + "\n\n" +
                    "Este código es válido por 10 minutos y solo puede usarse una vez.\n\n" +
                    "Si no solicitaste este código, ignora este mensaje.\n\n" +
                    "---\n" +
                    "PlannerApp - Planificador de Tareas");

            String response = client().post()
                    .uri(BREVO_SEND_URL)
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("Email enviado exitosamente a {} vía Brevo: {}", to, response);
            return true;
        } catch (Exception e) {
            log.error("Error enviando email a {} vía Brevo: {}", to, e.getMessage());
            return false;
        }
    }

    private String extractEmail(String value) {
        if (value == null || value.isBlank()) return "";
        int lt = value.indexOf('<');
        int gt = value.lastIndexOf('>');
        if (lt >= 0 && gt > lt) return value.substring(lt + 1, gt).trim();
        return value.trim();
    }
}
