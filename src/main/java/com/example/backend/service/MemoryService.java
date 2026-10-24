package com.example.backend.service;

import com.example.backend.dto.MemoryDto;
import com.example.backend.dto.MemoryResponseDto;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.Memory;
import com.example.backend.model.User;
import com.example.backend.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Transactional
    public MemoryResponseDto createMemory(String email, MemoryDto dto) {
        User user = userService.findByEmail(email);

        Memory memory = Memory.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .photoUrl(dto.getPhotoUrl())
                .memoryDate(dto.getMemoryDate())
                .nostalgiaLevel(dto.getNostalgiaLevel())
                .type(dto.getType() != null ? dto.getType() : "PHOTO")
                .createdAt(LocalDateTime.now())
                .build();

        Memory saved = memoryRepository.save(memory);
        return dtoMapper.toMemoryResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MemoryResponseDto> listMemories(String email) {
        User user = userService.findByEmail(email);
        return memoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(dtoMapper::toMemoryResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMemory(String email, Long id) {
        User user = userService.findByEmail(email);
        Memory memory = memoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found with id: " + id));

        if (!memory.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to delete this memory");
        }

        memoryRepository.delete(memory);
    }
}
