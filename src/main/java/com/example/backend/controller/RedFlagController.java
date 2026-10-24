package com.example.backend.controller;

import com.example.backend.dto.RedFlagDto;
import com.example.backend.dto.RedFlagResponseDto;
import com.example.backend.service.RedFlagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/red-flags")
@RequiredArgsConstructor
public class RedFlagController {

    private final RedFlagService redFlagService;

    @GetMapping
    public ResponseEntity<List<RedFlagResponseDto>> listRedFlags(Authentication auth) {
        return ResponseEntity.ok(redFlagService.listRedFlags(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<RedFlagResponseDto> createRedFlag(Authentication auth,
                                                           @Valid @RequestBody RedFlagDto dto) {
        RedFlagResponseDto saved = redFlagService.createRedFlag(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRedFlag(Authentication auth,
                                                @PathVariable Long id) {
        redFlagService.deleteRedFlag(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
