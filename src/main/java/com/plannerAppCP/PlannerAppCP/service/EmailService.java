package com.plannerAppCP.PlannerAppCP.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public void sendRecoveryCode(String to, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("PlannerApp - Tu código de verificación");
            message.setText(
                "Hola,\n\n" +
                "Tu código de verificación es: " + code + "\n\n" +
                "Este código es válido por 10 minutos y solo puede usarse una vez.\n\n" +
                "Si no solicitaste este código, ignora este mensaje.\n\n" +
                "---\n" +
                "PlannerApp - Planificador de Tareas"
            );
            mailSender.send(message);
            log.info("Email enviado exitosamente a {}", to);
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }
}
