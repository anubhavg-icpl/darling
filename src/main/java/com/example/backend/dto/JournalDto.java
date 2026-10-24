package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalDto {
    @NotBlank(message = "The entry title is required")
    private String title;

    @NotBlank(message = "The journal content is required")
    private String content;

    private String mood;
}
