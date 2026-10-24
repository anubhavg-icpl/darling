package com.example.backend.controller;

import com.example.backend.dto.MemoryDto;
import com.example.backend.dto.MemoryResponseDto;
import com.example.backend.service.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @GetMapping
    public ResponseEntity<List<MemoryResponseDto>> listMemories(Authentication auth) {
        return ResponseEntity.ok(memoryService.listMemories(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<MemoryResponseDto> createMemory(Authentication auth,
                                                             @Valid @RequestBody MemoryDto dto) {
        MemoryResponseDto saved = memoryService.createMemory(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemory(Authentication auth,
                                                 @PathVariable Long id) {
        memoryService.deleteMemory(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
