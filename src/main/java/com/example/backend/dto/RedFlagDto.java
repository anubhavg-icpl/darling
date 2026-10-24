package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedFlagDto {

    @NotBlank(message = "The red flag title is required")
    @Schema(description = "Title or name of the detected toxic behavior", example = "Gaslighting and manipulation")
    private String title;

    @Schema(description = "Details of the behavior", example = "He dismissed my feelings, saying I was exaggerating and that it was all my fault")
    private String description;

    @Min(value = 1, message = "The minimum severity is 1")
    @Max(value = 5, message = "The maximum severity is 5")
    @Schema(description = "Severity or toxicity level on a scale of 1 to 5", example = "4", defaultValue = "3")
    private int severity = 3;
}
