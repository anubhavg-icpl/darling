package com.example.backend.service;

import com.example.backend.dto.JournalDto;
import com.example.backend.dto.JournalResponseDto;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.JournalEntry;
import com.example.backend.model.User;
import com.example.backend.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalEntryRepository journalRepository;
    private final UserService userService;
    private final DtoMapper dtoMapper;

    @Transactional
    public JournalResponseDto createEntry(String email, JournalDto dto) {
        User user = userService.findByEmail(email);

        JournalEntry entry = JournalEntry.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .mood(dto.getMood() != null ? dto.getMood() : "IN_PROGRESS")
                .date(LocalDateTime.now())
                .build();

        JournalEntry saved = journalRepository.save(entry);
        return dtoMapper.toJournalResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<JournalResponseDto> listEntries(String email) {
        User user = userService.findByEmail(email);
        return journalRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream()
                .map(dtoMapper::toJournalResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEntry(String email, Long id) {
        User user = userService.findByEmail(email);
        JournalEntry entry = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with id: " + id));

        if (!entry.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to delete this journal entry");
        }

        journalRepository.delete(entry);
    }
}
