package com.plannerAppCP.PlannerAppCP.controller;

import com.plannerAppCP.PlannerAppCP.model.User;
import com.plannerAppCP.PlannerAppCP.repository.UserRepository;
import com.plannerAppCP.PlannerAppCP.service.EmailService;
import com.plannerAppCP.PlannerAppCP.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth", description = "Registro y recuperación de tareas por email")
public class AuthController {

    private static final SecureRandom random = new SecureRandom();
    private static final int RATE_LIMIT_MAX = 3;
    private static final long RATE_LIMIT_WINDOW_MS = 15 * 60 * 1000;
    private static final String MSG_CODIGO = "Si el email es válido, recibirás un código de verificación";

    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> sendAttempts = new ConcurrentHashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @Value("${email.demo-mode:true}")
    private boolean demoMode;

    private boolean safeSendEmail(String email, String code) {
        try {
            return emailService.sendRecoveryCode(email, code);
        } catch (Exception e) {
            System.err.println("Error enviando email a " + email + ": " + e.getMessage());
            return false;
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private ResponseEntity<?> respuestaEnvio(boolean enviado, String codigo) {
        if (!enviado && demoMode && codigo != null) {
            return ResponseEntity.ok(Map.of("message", MSG_CODIGO, "demoCode", codigo));
        }
        return ResponseEntity.ok(Map.of("message", MSG_CODIGO));
    }

    @Operation(summary = "Registrar email y enviar código de verificación")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = normalizeEmail(body.get("email"));
        String deviceId = body.get("deviceId");

        if (email == null || email.isEmpty() || deviceId == null || deviceId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y deviceId son requeridos"));
        }

        if (isRateLimited(email)) {
            return ResponseEntity.status(429).body(Map.of("error", "Demasiados intentos. Intenta de nuevo en 15 minutos."));
        }

        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            recordAttempt(email);
            String code = generateCode();
            user.setRecoverCode(code);
            user.setRecoverCodeExpires(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);
            return respuestaEnvio(safeSendEmail(email, code), code);
        }

        recordAttempt(email);
        User user = User.builder()
                .email(email)
                .deviceId(deviceId)
                .verified(false)
                .build();

        String code = generateCode();
        user.setRecoverCode(code);
        user.setRecoverCodeExpires(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        return respuestaEnvio(safeSendEmail(email, code), code);
    }

    @Operation(summary = "Enviar código de recuperación al email")
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String email = normalizeEmail(body.get("email"));

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email es requerido"));
        }

        if (isRateLimited(email)) {
            return ResponseEntity.status(429).body(Map.of("error", "Demasiados intentos. Intenta de nuevo en 15 minutos."));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", MSG_CODIGO));
        }

        User user = userOpt.get();
        recordAttempt(email);
        String code = generateCode();
        user.setRecoverCode(code);
        user.setRecoverCodeExpires(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        return respuestaEnvio(safeSendEmail(email, code), code);
    }

    @Operation(summary = "Verificar código y obtener token de acceso")
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> body) {
        String email = normalizeEmail(body.get("email"));
        String code = body.get("code");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email y código son requeridos"));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código incorrecto o expirado"));
        }

        User user = userOpt.get();

        if (user.getRecoverCode() == null || !code.equals(user.getRecoverCode())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código incorrecto o expirado"));
        }

        if (user.getRecoverCodeExpires() == null || user.getRecoverCodeExpires().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Código incorrecto o expirado"));
        }

        user.setRecoverCode(null);
        user.setRecoverCodeExpires(null);
        user.setVerified(true);
        userRepository.save(user);

        String token = tokenService.createToken(email);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "deviceId", user.getDeviceId() != null ? user.getDeviceId() : ""
        ));
    }

    private String generateCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private boolean isRateLimited(String email) {
        ConcurrentLinkedDeque<Long> attempts = sendAttempts.get(email);
        if (attempts == null) return false;

        long cutoff = System.currentTimeMillis() - RATE_LIMIT_WINDOW_MS;
        attempts.removeIf(t -> t < cutoff);
        return attempts.size() >= RATE_LIMIT_MAX;
    }

    private void recordAttempt(String email) {
        sendAttempts.computeIfAbsent(email, k -> new ConcurrentLinkedDeque<>())
                .add(System.currentTimeMillis());
    }
}
