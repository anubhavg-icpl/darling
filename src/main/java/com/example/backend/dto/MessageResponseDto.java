package com.example.backend.dto;

import com.example.backend.model.MessageStatus;
import com.example.backend.model.Sender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDto {
    private Long id;
    private String content;
    private Sender sender;
    private MessageStatus status;
    private LocalDateTime sentAt;
}
