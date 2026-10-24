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
public class ExDatasetUploadDto {

    @NotBlank(message = "The chat content is required for analysis")
    private String chatContent;

    private String exSenderName; // Name the ex uses in the exported chat (e.g. "Camila")
}
