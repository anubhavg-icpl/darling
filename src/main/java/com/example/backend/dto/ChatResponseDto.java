package com.example.backend.dto;

import com.example.backend.model.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDto {
    private Long id;
    private ChatType type;
    private LocalDateTime createdAt;
    private List<MessageResponseDto> messages;
}
