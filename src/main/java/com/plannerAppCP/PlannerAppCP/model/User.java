package com.plannerAppCP.PlannerAppCP.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email no válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "device_id", nullable = false, length = 64)
    private String deviceId;

    @Column(name = "recover_code", length = 10)
    private String recoverCode;

    @Column(name = "recover_code_expires")
    private LocalDateTime recoverCodeExpires;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
