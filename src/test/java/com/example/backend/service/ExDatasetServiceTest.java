package com.example.backend.service;

import com.example.backend.dto.ExDatasetAnalysisDto;
import com.example.backend.dto.ExDatasetUploadDto;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExDatasetServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExDatasetService exDatasetService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@nocontact.app")
                .name("Sam")
                .exName("Camila")
                .exPersonality("NOSTALGIC_DISTANT")
                .build();
    }

    @Test
    @DisplayName("Processing a JustZando-style exported chat extracts metrics and tunes the personality")
    void processingExportedChatExtractsMetricsAndTunesPersonality() {
        String chatExport = """
                [12/05/24, 22:10] Sam: Hi Camila, how are you?
                [12/05/24, 22:11] Camila: Hi. Busy. 👍
                [12/05/24, 22:12] Sam: I just wanted to hear from you...
                [12/05/24, 22:15] Camila: Stop texting me. Ok. 🙄
                """;

        ExDatasetUploadDto uploadDto = ExDatasetUploadDto.builder()
                .chatContent(chatExport)
                .exSenderName("Camila")
                .build();

        when(userService.findByEmail("test@nocontact.app")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ExDatasetAnalysisDto result = exDatasetService.processAndTrainDataset("test@nocontact.app", uploadDto);

        assertNotNull(result);
        assertEquals(2, result.getTotalExMessages());
        assertTrue(result.getTotalLinesProcessed() >= 4);
        assertNotNull(result.getDetectedTone());
        verify(userRepository, times(1)).save(user);
        assertNotNull(user.getExTrainingDataset());
    }
}
