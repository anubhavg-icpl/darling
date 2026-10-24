package com.example.backend.service;

import com.example.backend.dto.AdConfigDto;
import com.example.backend.dto.GlobalMetricsDto;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.AdConfig;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final NoContactRelapseRepository relapseRepository;
    private final JournalEntryRepository journalRepository;
    private final RedFlagRepository redFlagRepository;
    private final MessageRepository messageRepository;
    private final AdConfigRepository adConfigRepository;

    @Transactional(readOnly = true)
    public GlobalMetricsDto getGlobalMetrics() {
        return GlobalMetricsDto.builder()
                .totalUsers(userRepository.count())
                .totalRelapsesRecorded(relapseRepository.count())
                .totalJournalEntries(journalRepository.count())
                .totalRedFlags(redFlagRepository.count())
                .totalMessagesExchanged(messageRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdConfigDto> listAllAds() {
        return adConfigRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdConfigDto> listActiveAds() {
        return adConfigRepository.findByActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdConfigDto createAd(AdConfigDto dto) {
        if (adConfigRepository.findByKey(dto.getKey()).isPresent()) {
            throw new DuplicateResourceException("A configuration with this key already exists: " + dto.getKey());
        }

        AdConfig config = AdConfig.builder()
                .key(dto.getKey().trim().toUpperCase())
                .title(dto.getTitle())
                .adUnitId(dto.getAdUnitId())
                .active(dto.isActive())
                .messageFrequency(dto.getMessageFrequency() > 0 ? dto.getMessageFrequency() : 5)
                .description(dto.getDescription())
                .build();

        return toDto(adConfigRepository.save(config));
    }

    @Transactional
    public AdConfigDto toggleStatus(Long id) {
        AdConfig config = adConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ad configuration not found with id: " + id));

        config.setActive(!config.isActive());
        return toDto(adConfigRepository.save(config));
    }

    private AdConfigDto toDto(AdConfig config) {
        return AdConfigDto.builder()
                .id(config.getId())
                .key(config.getKey())
                .title(config.getTitle())
                .adUnitId(config.getAdUnitId())
                .active(config.isActive())
                .messageFrequency(config.getMessageFrequency())
                .description(config.getDescription())
                .build();
    }
}
