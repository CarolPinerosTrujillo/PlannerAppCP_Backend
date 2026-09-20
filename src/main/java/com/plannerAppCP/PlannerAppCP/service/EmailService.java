package com.plannerAppCP.PlannerAppCP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRecoveryCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("PlannerApp - Código de recuperación");
        message.setText(
            "Hola,\n\n" +
            "Tu código de recuperación es: " + code + "\n\n" +
            "Este código es válido por 10 minutos.\n\n" +
            "Si no solicitaste este código, ignora este mensaje."
        );
        mailSender.send(message);
    }
}
