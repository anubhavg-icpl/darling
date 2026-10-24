package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalMetricsDto {
    private long totalUsers;
    private long totalRelapsesRecorded;
    private long totalJournalEntries;
    private long totalRedFlags;
    private long totalMessagesExchanged;
}
