package com.example.backend.dto;

import com.example.backend.model.AppMode;
import com.example.backend.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private AppMode currentMode;
    private String exName;
    private String exNickname;
    private String exProfilePhoto;
    private String exWhatsAppStatus;
    private String exPersonality;
    private LocalDate breakupDate;
    private LocalDateTime noContactStartDate;
    private int noContactStreakDays;
    private int maxNoContactStreak;
    private LocalDateTime registeredAt;
}
