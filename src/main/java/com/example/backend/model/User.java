package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppMode currentMode = AppMode.REMEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    // --- Ex profile for the WhatsApp-style appearance ---
    @Builder.Default
    private String exName = "My Ex";

    private String exNickname;

    private String exProfilePhoto;

    @Builder.Default
    private String exWhatsAppStatus = "online";

    @Builder.Default
    private String exPersonality = "NOSTALGIC_DISTANT"; // COLD, AFFECTIONATE, TOXIC, NOSTALGIC_DISTANT

    @Column(columnDefinition = "TEXT")
    private String exTrainingDataset;

    // --- Breakup and No Contact metrics ---
    private LocalDate breakupDate;

    private LocalDateTime noContactStartDate;

    @Builder.Default
    private int noContactStreakDays = 0;

    @Builder.Default
    private int maxNoContactStreak = 0;

    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
}
