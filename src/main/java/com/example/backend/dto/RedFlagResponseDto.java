package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedFlagResponseDto {

    @Schema(description = "Unique identifier of the red flag", example = "1")
    private Long id;

    @Schema(description = "Red flag title", example = "Gaslighting and manipulation")
    private String title;

    @Schema(description = "Description of the behavior", example = "He dismissed my feelings by saying I was exaggerating")
    private String description;

    @Schema(description = "Severity level (1 to 5)", example = "4")
    private int severity;

    @Schema(description = "Registration date", example = "2026-09-25T11:00:00")
    private LocalDateTime createdAt;
}
