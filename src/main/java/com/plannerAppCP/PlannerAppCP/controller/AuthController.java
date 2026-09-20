package com.plannerAppCP.PlannerAppCP.controller;

import com.plannerAppCP.PlannerAppCP.model.User;
import com.plannerAppCP.PlannerAppCP.repository.UserRepository;
import com.plannerAppCP.PlannerAppCP.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "Registro y recuperación de tareas por email")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Registrar email con device_id")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String deviceId = body.get("deviceId");

        if (email == null || email.isEmpty() || deviceId == null || deviceId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y deviceId son requeridos"));
        }

        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            user.setDeviceId(deviceId);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Email actualizado correctamente"));
        }

        User user = User.builder()
                .email(email)
                .deviceId(deviceId)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Email registrado correctamente"));
    }

    @Operation(summary = "Enviar código de recuperación al email")
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email es requerido"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email no registrado"));
        }

        User user = userOpt.get();
        String code = String.format("%06d", new Random().nextInt(999999));
        user.setRecoverCode(code);
        user.setRecoverCodeExpires(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendRecoveryCode(email, code);
        return ResponseEntity.ok(Map.of("message", "Código enviado a tu email"));
    }

    @Operation(summary = "Verificar código y obtener device_id")
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y código son requeridos"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email no registrado"));
        }

        User user = userOpt.get();
        if (!code.equals(user.getRecoverCode())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código incorrecto"));
        }
        if (user.getRecoverCodeExpires().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código expirado"));
        }

        return ResponseEntity.ok(Map.of("deviceId", user.getDeviceId()));
    }
}
