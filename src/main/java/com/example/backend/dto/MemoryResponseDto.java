package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class MemoryResponseDto {

    @Schema(description = "Unique identifier of the memory", example = "1")
    private Long id;

    @Schema(description = "Memory title", example = "Trip to the beach")
    private String title;

    @Schema(description = "Memory description", example = "The day we went south and watched the sunset")
    private String description;

    @Schema(description = "Photo URL", example = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e")
    private String photoUrl;

    @Schema(description = "Memory date", example = "2025-02-14")
    private LocalDate memoryDate;

    @Schema(description = "Nostalgia level (1 to 5)", example = "4")
    private int nostalgiaLevel;

    @Schema(description = "Memory type", example = "PHOTO")
    private String type;

    @Schema(description = "Date registered in the app", example = "2026-09-25T11:00:00")
    private LocalDateTime createdAt;
}
