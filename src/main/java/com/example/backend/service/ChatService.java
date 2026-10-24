package com.example.backend.service;

import com.example.backend.dto.ChatResponseDto;
import com.example.backend.dto.SendMessageDto;
import com.example.backend.dto.MessageResponseDto;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.*;
import com.example.backend.repository.ChatRepository;
import com.example.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ExAiSimulatorService exAiSimulatorService;
    private final DtoMapper dtoMapper;

    @Transactional
    public ChatResponseDto getOrCreateChat(String email, ChatType chatType) {
        User user = userService.findByEmail(email);

        Chat chat = chatRepository.findByUserIdAndChatType(user.getId(), chatType)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .user(user)
                            .chatType(chatType)
                            .title(chatType == ChatType.CHAT_EX ? user.getExName() : "Move-On Assistant")
                            .lastMessage("Chat started")
                            .lastMessageAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(newChat);
                });

        List<Message> messages = messageRepository.findByChatIdOrderByTimestampAsc(chat.getId());
        return dtoMapper.toChatResponseDto(chat, messages);
    }

    @Transactional(readOnly = true)
    public List<ChatResponseDto> listUserChats(String email) {
        User user = userService.findByEmail(email);
        List<Chat> chats = chatRepository.findByUserIdOrderByLastMessageAtDesc(user.getId());

        return chats.stream().map(c -> {
            List<Message> messages = messageRepository.findByChatIdOrderByTimestampAsc(c.getId());
            return dtoMapper.toChatResponseDto(c, messages);
        }).collect(Collectors.toList());
    }

    @Transactional
    public MessageResponseDto sendMessage(String email, Long chatId, SendMessageDto dto) {
        User user = userService.findByEmail(email);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));

        if (!chat.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to interact in this chat");
        }

        // 1. Save the user message
        Message userMessage = Message.builder()
                .chat(chat)
                .sender(Sender.USER)
                .content(dto.getContent())
                .status(MessageStatus.READ)
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(userMessage);

        // 2. Generate the AI reply based on mode and personality
        String replyText = exAiSimulatorService.generateResponse(
                user,
                dto.getContent(),
                user.getCurrentMode()
        );

        // 3. Save the bot message (Ex or Support)
        Message botMessage = Message.builder()
                .chat(chat)
                .sender(chat.getChatType() == ChatType.CHAT_EX ? Sender.EX_BOT : Sender.SYSTEM)
                .content(replyText)
                .status(MessageStatus.READ)
                .timestamp(LocalDateTime.now())
                .build();
        Message savedBot = messageRepository.save(botMessage);

        // 4. Update chat metadata
        chat.setLastMessage(replyText);
        chat.setLastMessageAt(LocalDateTime.now());
        chatRepository.save(chat);

        return dtoMapper.toMessageResponseDto(savedBot);
    }

    @Transactional
    public void clearHistory(String email, Long chatId) {
        User user = userService.findByEmail(email);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));

        if (!chat.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to modify this chat");
        }

        List<Message> messages = messageRepository.findByChatIdOrderByTimestampAsc(chatId);
        messageRepository.deleteAll(messages);

        chat.setLastMessage("History cleared");
        chat.setLastMessageAt(LocalDateTime.now());
        chatRepository.save(chat);
    }
}
