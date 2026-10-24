package com.example.backend.mapper;

import com.example.backend.dto.*;
import com.example.backend.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public UserResponseDto toUserResponseDto(User u) {
        if (u == null) return null;
        return UserResponseDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .role(u.getRole())
                .currentMode(u.getCurrentMode())
                .exName(u.getExName())
                .exNickname(u.getExNickname())
                .exProfilePhoto(u.getExProfilePhoto())
                .exWhatsAppStatus(u.getExWhatsAppStatus())
                .exPersonality(u.getExPersonality())
                .breakupDate(u.getBreakupDate())
                .noContactStartDate(u.getNoContactStartDate())
                .noContactStreakDays(u.getNoContactStreakDays())
                .maxNoContactStreak(u.getMaxNoContactStreak())
                .registeredAt(u.getRegisteredAt())
                .build();
    }

    public MessageResponseDto toMessageResponseDto(Message m) {
        if (m == null) return null;
        return MessageResponseDto.builder()
                .id(m.getId())
                .content(m.getContent())
                .sender(m.getSender())
                .status(m.getStatus())
                .sentAt(m.getTimestamp())
                .build();
    }

    public ChatResponseDto toChatResponseDto(Chat c, List<Message> messages) {
        if (c == null) return null;
        List<MessageResponseDto> messagesDto = (messages == null)
                ? Collections.emptyList()
                : messages.stream().map(this::toMessageResponseDto).collect(Collectors.toList());

        return ChatResponseDto.builder()
                .id(c.getId())
                .type(c.getChatType())
                .createdAt(c.getCreatedAt())
                .messages(messagesDto)
                .build();
    }

    public JournalResponseDto toJournalResponseDto(JournalEntry d) {
        if (d == null) return null;
        return JournalResponseDto.builder()
                .id(d.getId())
                .title(d.getTitle())
                .content(d.getContent())
                .mood(d.getMood())
                .createdAt(d.getDate())
                .build();
    }

    public RelapseResponseDto toRelapseResponseDto(NoContactRelapse r) {
        if (r == null) return null;
        return RelapseResponseDto.builder()
                .id(r.getId())
                .reason(r.getReason())
                .relapsedAt(r.getRelapsedAt())
                .build();
    }

    public RedFlagResponseDto toRedFlagResponseDto(RedFlag rf) {
        if (rf == null) return null;
        return RedFlagResponseDto.builder()
                .id(rf.getId())
                .title(rf.getTitle())
                .description(rf.getDescription())
                .severity(rf.getSeverity())
                .createdAt(rf.getRegisteredAt())
                .build();
    }

    public MemoryResponseDto toMemoryResponseDto(Memory r) {
        if (r == null) return null;
        return MemoryResponseDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .photoUrl(r.getPhotoUrl())
                .memoryDate(r.getMemoryDate())
                .nostalgiaLevel(r.getNostalgiaLevel())
                .type(r.getType())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
