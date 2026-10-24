package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdConfigDto {
    private Long id;

    @NotBlank(message = "The ad key is required")
    private String key;

    private String title;
    private String adUnitId;
    private boolean active;
    private int messageFrequency;
    private String description;
}
