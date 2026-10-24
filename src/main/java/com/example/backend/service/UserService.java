package com.example.backend.service;

import com.example.backend.dto.ChangeModeDto;
import com.example.backend.dto.ExConfigDto;
import com.example.backend.dto.UserResponseDto;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public UserResponseDto getProfile(String email) {
        User user = findByEmail(email);
        return dtoMapper.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateExConfig(String email, ExConfigDto dto) {
        User user = findByEmail(email);

        if (dto.getExName() != null && !dto.getExName().isBlank()) {
            user.setExName(dto.getExName());
        }
        if (dto.getExNickname() != null) {
            user.setExNickname(dto.getExNickname());
        }
        if (dto.getExProfilePhoto() != null) {
            user.setExProfilePhoto(dto.getExProfilePhoto());
        }
        if (dto.getExWhatsAppStatus() != null) {
            user.setExWhatsAppStatus(dto.getExWhatsAppStatus());
        }
        if (dto.getExPersonality() != null) {
            user.setExPersonality(dto.getExPersonality());
        }
        if (dto.getBreakupDate() != null) {
            user.setBreakupDate(dto.getBreakupDate());
        }

        User updated = userRepository.save(user);
        return dtoMapper.toUserResponseDto(updated);
    }

    @Transactional
    public UserResponseDto changeMode(String email, ChangeModeDto dto) {
        User user = findByEmail(email);
        user.setCurrentMode(dto.getMode());
        User updated = userRepository.save(user);
        return dtoMapper.toUserResponseDto(updated);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
