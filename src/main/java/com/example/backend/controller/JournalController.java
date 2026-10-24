package com.example.backend.controller;

import com.example.backend.dto.JournalDto;
import com.example.backend.dto.JournalResponseDto;
import com.example.backend.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @GetMapping
    public ResponseEntity<List<JournalResponseDto>> listEntries(Authentication auth) {
        return ResponseEntity.ok(journalService.listEntries(auth.getName()));
    }

    @PostMapping
    public ResponseEntity<JournalResponseDto> createEntry(Authentication auth,
                                                          @Valid @RequestBody JournalDto dto) {
        JournalResponseDto saved = journalService.createEntry(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(Authentication auth,
                                                @PathVariable Long id) {
        journalService.deleteEntry(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
