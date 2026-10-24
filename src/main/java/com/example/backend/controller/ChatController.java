package com.example.backend.controller;

import com.example.backend.dto.ChatResponseDto;
import com.example.backend.dto.SendMessageDto;
import com.example.backend.dto.MessageResponseDto;
import com.example.backend.model.ChatType;
import com.example.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatResponseDto>> listChats(Authentication auth) {
        return ResponseEntity.ok(chatService.listUserChats(auth.getName()));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ChatResponseDto> getChatByType(Authentication auth,
                                                              @PathVariable String type) {
        ChatType chatType = (type.equalsIgnoreCase("ex") || type.equalsIgnoreCase("chat_ex"))
                ? ChatType.CHAT_EX : ChatType.CHAT_SUPPORT;
        return ResponseEntity.ok(chatService.getOrCreateChat(auth.getName(), chatType));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(Authentication auth,
                                                            @PathVariable Long chatId,
                                                            @Valid @RequestBody SendMessageDto dto) {
        MessageResponseDto botReply = chatService.sendMessage(auth.getName(), chatId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(botReply);
    }

    @DeleteMapping("/{chatId}/messages")
    public ResponseEntity<Void> clearHistory(Authentication auth,
                                                 @PathVariable Long chatId) {
        chatService.clearHistory(auth.getName(), chatId);
        return ResponseEntity.noContent().build();
    }
}
