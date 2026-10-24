package com.example.backend.repository;

import com.example.backend.model.Chat;
import com.example.backend.model.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserIdOrderByLastMessageAtDesc(Long userId);
    Optional<Chat> findByUserIdAndChatType(Long userId, ChatType chatType);
}
