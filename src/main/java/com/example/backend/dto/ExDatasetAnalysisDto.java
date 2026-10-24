package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExDatasetAnalysisDto {
    private int totalLinesProcessed;
    private int totalExMessages;
    private int totalUserMessages;
    private double averageExMessageLength;
    private List<String> frequentExPhrases;
    private List<String> frequentEmojis;
    private String detectedTone;
    private String message;
}
