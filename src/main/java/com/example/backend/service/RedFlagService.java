package com.example.backend.service;

import com.example.backend.dto.RedFlagDto;
import com.example.backend.dto.RedFlagResponseDto;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.RedFlag;
import com.example.backend.model.User;
import com.example.backend.repository.RedFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedFlagService {

    private final RedFlagRepository redFlagRepository;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Transactional
    public RedFlagResponseDto createRedFlag(String email, RedFlagDto dto) {
        User user = userService.findByEmail(email);

        RedFlag redFlag = RedFlag.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .severity(dto.getSeverity())
                .registeredAt(LocalDateTime.now())
                .build();

        RedFlag saved = redFlagRepository.save(redFlag);
        return dtoMapper.toRedFlagResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RedFlagResponseDto> listRedFlags(String email) {
        User user = userService.findByEmail(email);
        return redFlagRepository.findByUserIdOrderBySeverityDesc(user.getId())
                .stream()
                .map(dtoMapper::toRedFlagResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRedFlag(String email, Long id) {
        User user = userService.findByEmail(email);
        RedFlag redFlag = redFlagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Red flag not found with id: " + id));

        if (!redFlag.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to delete this red flag");
        }

        redFlagRepository.delete(redFlag);
    }
}
