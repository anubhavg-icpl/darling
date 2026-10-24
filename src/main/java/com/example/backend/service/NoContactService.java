package com.example.backend.service;

import com.example.backend.dto.NoContactStatisticsDto;
import com.example.backend.dto.RelapseDto;
import com.example.backend.dto.RelapseResponseDto;
import com.example.backend.event.RelapseRecordedEvent;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.NoContactRelapse;
import com.example.backend.model.User;
import com.example.backend.repository.NoContactRelapseRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoContactService {

    private final NoContactRelapseRepository relapseRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final DtoMapper dtoMapper;

    @Transactional
    public RelapseResponseDto recordRelapse(String email, RelapseDto dto) {
        User user = userService.findByEmail(email);

        int daysReached = calculateCurrentDays(user);

        // Update the max streak if the current streak exceeds it
        if (daysReached > user.getMaxNoContactStreak()) {
            user.setMaxNoContactStreak(daysReached);
        }

        // Reset the no-contact counter
        user.setNoContactStreakDays(0);
        user.setNoContactStartDate(LocalDateTime.now());
        userRepository.save(user);

        // Save the historical relapse record
        NoContactRelapse relapse = NoContactRelapse.builder()
                .user(user)
                .daysReached(daysReached)
                .reason(dto.getReason())
                .relapsedAt(LocalDateTime.now())
                .build();
        NoContactRelapse saved = relapseRepository.save(relapse);

        // Publish an async event for the alert and support email
        eventPublisher.publishEvent(new RelapseRecordedEvent(this, user, saved));

        return dtoMapper.toRelapseResponseDto(saved);
    }

    @Transactional
    public NoContactStatisticsDto getStatistics(String email) {
        User user = userService.findByEmail(email);
        int currentDays = calculateCurrentDays(user);

        if (currentDays != user.getNoContactStreakDays()) {
            user.setNoContactStreakDays(currentDays);
            if (currentDays > user.getMaxNoContactStreak()) {
                user.setMaxNoContactStreak(currentDays);
            }
            userRepository.save(user);
        }

        long totalRelapses = relapseRepository.countByUserId(user.getId());

        return NoContactStatisticsDto.builder()
                .currentStreakDays(user.getNoContactStreakDays())
                .maxStreak(user.getMaxNoContactStreak())
                .totalRelapses(totalRelapses)
                .noContactStartDate(user.getNoContactStartDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RelapseResponseDto> listRelapses(String email) {
        User user = userService.findByEmail(email);
        return relapseRepository.findByUserIdOrderByRelapsedAtDesc(user.getId())
                .stream()
                .map(dtoMapper::toRelapseResponseDto)
                .collect(Collectors.toList());
    }

    private int calculateCurrentDays(User user) {
        if (user.getNoContactStartDate() == null) {
            user.setNoContactStartDate(LocalDateTime.now());
            return 0;
        }
        long days = Duration.between(user.getNoContactStartDate(), LocalDateTime.now()).toDays();
        return (int) Math.max(0, days);
    }
}
