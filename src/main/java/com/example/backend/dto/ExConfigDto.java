package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExConfigDto {
    private String exName;
    private String exNickname;
    private String exProfilePhoto;
    private String exWhatsAppStatus;
    private String exPersonality;
    private LocalDate breakupDate;
}
