package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryDto {

    @NotBlank(message = "The memory title is required")
    @Schema(description = "Memory title", example = "Trip to the beach")
    private String title;

    @Schema(description = "Detailed description of the memory", example = "The day we went south and watched the sunset")
    private String description;

    @Schema(description = "Photo URL", example = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e")
    private String photoUrl;

    @Schema(description = "Date the memory happened", example = "2025-02-14")
    private LocalDate memoryDate;

    @Min(value = 1, message = "The minimum nostalgia level is 1")
    @Max(value = 5, message = "The maximum nostalgia level is 5")
    @Schema(description = "Nostalgia level from 1 to 5", example = "4", defaultValue = "3")
    private int nostalgiaLevel = 3;

    @Schema(description = "Memory type (PHOTO, LETTER, SONG, PLACE)", example = "PHOTO")
    private String type;
}
