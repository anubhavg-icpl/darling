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
public class NoContactStatisticsDto {

    @Schema(description = "Accumulated consecutive days of no contact", example = "15")
    private int currentStreakDays;

    @Schema(description = "All-time longest streak without relapses", example = "42")
    private int maxStreak;

    @Schema(description = "Total number of recorded relapses", example = "2")
    private long totalRelapses;

    @Schema(description = "Start date of the current count", example = "2026-09-10T00:00:00")
    private LocalDateTime noContactStartDate;
}
