package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ad_configs")
public class AdConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Column is "config_key", not "key": KEY is a reserved word in H2 (used by the test profile) and
    // would break schema generation there.
    @Column(name = "config_key", nullable = false, unique = true)
    private String key; // e.g. "BANNER_CHAT", "INTERSTITIAL_MODE", "REWARDED_MEMORY"

    private String title;

    private String adUnitId; // AdMob or ad network unit ID

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private int messageFrequency = 5; // Show every N messages

    private String description;
}
