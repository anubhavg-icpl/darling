package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.ExDatasetService;
import com.example.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users & Profile", description = "Profile management, modes and AI clone configuration")
public class UserController {

    private final UserService userService;
    private final ExDatasetService exDatasetService;

    @Operation(summary = "Get the authenticated user profile")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile(Authentication auth) {
        UserResponseDto profile = userService.getProfile(auth.getName());
        return ResponseEntity.ok(profile);
    }

    @Operation(summary = "Update the ex profile settings (WhatsApp appearance)")
    @PutMapping("/ex-config")
    public ResponseEntity<UserResponseDto> updateExConfig(Authentication auth,
                                                                 @RequestBody ExConfigDto dto) {
        UserResponseDto updated = userService.updateExConfig(auth.getName(), dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Change the application mode (REMEMBER or MOVE_ON)")
    @PatchMapping("/mode")
    public ResponseEntity<UserResponseDto> changeMode(Authentication auth,
                                                          @Valid @RequestBody ChangeModeDto dto) {
        UserResponseDto updated = userService.changeMode(auth.getName(), dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Upload and train the ex chat dataset (inspired by JustZando)",
               description = "Processes the chat history exported from WhatsApp or social networks and detects the ex's tone, emojis and slang.")
    @PostMapping("/ex-dataset")
    public ResponseEntity<ExDatasetAnalysisDto> uploadExDataset(Authentication auth,
                                                              @Valid @RequestBody ExDatasetUploadDto dto) {
        ExDatasetAnalysisDto result = exDatasetService.processAndTrainDataset(auth.getName(), dto);
        return ResponseEntity.ok(result);
    }
}
