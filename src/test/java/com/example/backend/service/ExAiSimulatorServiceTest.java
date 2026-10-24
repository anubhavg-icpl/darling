package com.example.backend.service;

import com.example.backend.model.AppMode;
import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExAiSimulatorServiceTest {

    private ExAiSimulatorService simulatorService;
    private User testUser;

    @BeforeEach
    void setUp() {
        simulatorService = new ExAiSimulatorService();
        testUser = User.builder()
                .id(1L)
                .name("Rick")
                .email("rick@dimensionc137.com")
                .exName("Diane")
                .exPersonality("Cold and distant")
                .currentMode(AppMode.REMEMBER)
                .build();
    }

    @Test
    @DisplayName("MOVE_ON mode must return emotional support messages")
    void moveOnModeReturnsSupportCatalogMessage() {
        String response = simulatorService.generateResponse(testUser, "I want to text them, I can't take it anymore", AppMode.MOVE_ON);

        assertNotNull(response);
        assertFalse(response.isBlank());
        assertTrue(
                response.contains("Move-On Assistant") ||
                response.contains("Mental Strength") ||
                response.contains("Reality") ||
                response.contains("Progress"),
                "The response must belong to the MOVE_ON mode support catalog"
        );
    }

    @Test
    @DisplayName("REMEMBER mode with a 'miss you' message must simulate rejection or distance")
    void rememberModeMissYouMessageSimulatesDistance() {
        String response = simulatorService.generateResponse(testUser, "I miss you so much, please come back", AppMode.REMEMBER);

        assertNotNull(response);
        assertFalse(response.isBlank());
        assertTrue(
                response.contains("harder") ||
                response.contains("hurting") ||
                response.contains("move forward") ||
                response.contains("too late") ||
                response.contains("Read") ||
                response.contains("take care") ||
                response.contains("Ok") ||
                response.contains("👍"),
                "The ex simulation must respond with realistic distance"
        );
    }
}
