package com.example.backend.controller;

import com.example.backend.dto.NoContactStatisticsDto;
import com.example.backend.dto.RelapseDto;
import com.example.backend.dto.RelapseResponseDto;
import com.example.backend.service.NoContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/no-contact")
@RequiredArgsConstructor
public class NoContactController {

    private final NoContactService noContactService;

    @GetMapping("/statistics")
    public ResponseEntity<NoContactStatisticsDto> getStatistics(Authentication auth) {
        return ResponseEntity.ok(noContactService.getStatistics(auth.getName()));
    }

    @GetMapping("/relapses")
    public ResponseEntity<List<RelapseResponseDto>> listRelapses(Authentication auth) {
        return ResponseEntity.ok(noContactService.listRelapses(auth.getName()));
    }

    @PostMapping("/relapses")
    public ResponseEntity<RelapseResponseDto> recordRelapse(Authentication auth,
                                                               @Valid @RequestBody RelapseDto dto) {
        RelapseResponseDto response = noContactService.recordRelapse(auth.getName(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
