package com.example.backend.service;

import com.example.backend.dto.NoContactStatisticsDto;
import com.example.backend.dto.RelapseDto;
import com.example.backend.dto.RelapseResponseDto;
import com.example.backend.mapper.DtoMapper;
import com.example.backend.model.NoContactRelapse;
import com.example.backend.model.User;
import com.example.backend.repository.NoContactRelapseRepository;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoContactServiceTest {

    @Mock
    private NoContactRelapseRepository relapseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private DtoMapper dtoMapper;

    @InjectMocks
    private NoContactService noContactService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@nocontact.app")
                .name("User Test")
                .noContactStreakDays(10)
                .maxNoContactStreak(15)
                .noContactStartDate(LocalDateTime.now().minusDays(5))
                .build();
    }

    @Test
    @DisplayName("Recording a relapse resets the current streak to 0 and emits an event")
    void recordingRelapseResetsStreakAndEmitsEvent() {
        RelapseDto relapseDto = new RelapseDto("I watched their Instagram stories in the middle of the night");

        when(userService.findByEmail("test@nocontact.app")).thenReturn(user);
        when(relapseRepository.save(any(NoContactRelapse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RelapseResponseDto expectedResponse = RelapseResponseDto.builder()
                .id(100L)
                .reason(relapseDto.getReason())
                .relapsedAt(LocalDateTime.now())
                .build();
        when(dtoMapper.toRelapseResponseDto(any(NoContactRelapse.class))).thenReturn(expectedResponse);

        RelapseResponseDto result = noContactService.recordRelapse("test@nocontact.app", relapseDto);

        assertNotNull(result);
        assertEquals(0, user.getNoContactStreakDays(), "The user streak must be reset to 0");
        verify(userRepository, times(1)).save(user);
        verify(relapseRepository, times(1)).save(any(NoContactRelapse.class));
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    @DisplayName("Getting statistics computes streaks and the total relapse count correctly")
    void statisticsReportMaxStreakAndRelapseTotal() {
        when(userService.findByEmail("test@nocontact.app")).thenReturn(user);
        when(relapseRepository.countByUserId(1L)).thenReturn(2L);

        NoContactStatisticsDto stats = noContactService.getStatistics("test@nocontact.app");

        assertNotNull(stats);
        assertEquals(15, stats.getMaxStreak());
        assertEquals(2L, stats.getTotalRelapses());
    }
}
