package com.example.backend.controller;

import com.example.backend.dto.AdConfigDto;
import com.example.backend.dto.GlobalMetricsDto;
import com.example.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Endpoints with strict authorization for administrators")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get global system metrics", description = "Requires ROLE_ADMIN")
    @GetMapping("/global-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalMetricsDto> getGlobalMetrics() {
        return ResponseEntity.ok(adminService.getGlobalMetrics());
    }

    @Operation(summary = "List all ad configurations", description = "Requires ROLE_ADMIN")
    @GetMapping("/ads")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdConfigDto>> listAllAds() {
        return ResponseEntity.ok(adminService.listAllAds());
    }

    @Operation(summary = "Create a new ad or banner configuration", description = "Requires ROLE_ADMIN")
    @PostMapping("/ads")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdConfigDto> createAd(@Valid @RequestBody AdConfigDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAd(dto));
    }

    @Operation(summary = "Enable or disable an ad by ID", description = "Requires ROLE_ADMIN")
    @PatchMapping("/ads/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdConfigDto> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleStatus(id));
    }

    @Operation(summary = "List active ads for mobile or web clients")
    @GetMapping("/ads/active")
    public ResponseEntity<List<AdConfigDto>> listActiveAds() {
        return ResponseEntity.ok(adminService.listActiveAds());
    }
}
