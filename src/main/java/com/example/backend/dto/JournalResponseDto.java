package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalResponseDto {
    private Long id;
    private String title;
    private String content;
    private String mood;
    private LocalDateTime createdAt;
}
